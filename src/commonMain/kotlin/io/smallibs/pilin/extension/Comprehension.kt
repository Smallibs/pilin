package io.smallibs.pilin.extension

import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.core.execute
import io.smallibs.pilin.type.App
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

class Comprehension<F, A>(private val monad: Monad.Core<F>) : Monad.Core<F> by monad {

    private class ComprehensionContinuation<F, A> : Continuation<App<F, A>> {
        private var result: Result<App<F, *>>? = null

        private var intermediate: Result<App<F, *>>? = null

        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<App<F, A>>) {
            this.result = result
        }

        fun <B> setResult(result: App<F, B>) {
            this.result = Result.success(result)
        }

        fun <B> setIntermediate(result: App<F, B>) {
            this.intermediate = Result.success(result)
        }

        @Suppress("UNCHECKED_CAST")
        suspend fun getResult(): App<F, A> {
            while (result == null) {
                suspendCoroutine<Unit> { cont -> cont.resume(Unit) }
            }
            return (result as Result<App<F, A>>).getOrThrow()
        }

        fun hasResult(): Boolean = result != null
    }

    private val continuation = ComprehensionContinuation<F, A>()

    suspend fun <B> App<F, B>.bind(): B =
        currentCoroutineContext().let { coroutineContext ->
            suspendCoroutineUninterceptedOrReturn { cont ->
                execute(coroutineContext) {
                    var unbound = true
                    continuation.setIntermediate(monad.map<B, B> {
                        unbound = false
                        cont.resume(it)
                        it
                    }(this))
                    if (unbound) {
                        continuation.setResult(this)
                    }
                }
                COROUTINE_SUSPENDED
            }
        }

    companion object {
        suspend infix fun <F, A> Monad.Core<F>.`do`(f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            invoke(this, f)

        suspend operator fun <F, A> invoke(monad: Monad.Core<F>, f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            Comprehension<F, A>(monad).let { comprehension ->
                val block: suspend Comprehension<F, A>.() -> App<F, A> = {
                    val result: App<F, A> = returns(comprehension.f())
                    this.continuation.resumeWith(Result.success(result))
                    result
                }
                block.startCoroutine(comprehension, comprehension.continuation).let {
                    while (!comprehension.continuation.hasResult()) {
                        // Force suspension of the while loop
                        suspendCoroutine<Unit> { cont -> cont.resume(Unit) }
                    }
                    comprehension.continuation.getResult()
                }
            }
    }
}