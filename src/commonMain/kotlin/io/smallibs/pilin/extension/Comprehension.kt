package io.smallibs.pilin.extension

import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.type.App
import kotlin.coroutines.*

class Comprehension<F, A>(private val monad: Monad.Core<F>) : Monad.Core<F> by monad {

    private class ComprehensionCompleted(private val app: App<*, *>) : Exception() {
        fun <F, A> get(): App<F, A> = app as App<F, A>
    }

    private class ComprehensionContinuation<F, A> : Continuation<App<F, A>> {
        private var result: Result<App<F, *>>? = null

        private var intermediate: Result<App<F, *>>? = null

        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<App<F, A>>) {
            this.result = result
        }

        fun <B> setIntermediate(result: App<F, B>) {
            this.intermediate = Result.success(result)
        }

        @Suppress("UNCHECKED_CAST")
        suspend fun getResult(): App<F, A> {
            while (result == null) {
                // Force while loop suspension
                suspendCoroutine<Unit> { cont -> cont.resume(Unit) }
            }
            return (result as Result<App<F, A>>).getOrThrow()
        }
    }

    private val continuation = ComprehensionContinuation<F, A>()

    suspend operator fun <B> App<F, B>.component1(): B =
        this.bind()

    suspend fun <B> App<F, B>.bind(): B {
        var value: B? = null

        monad.map<B, Unit> {
            value = it
        }(this)

        if (value != null) {
            continuation.setIntermediate(this)
            return value!!
        } else {
            throw ComprehensionCompleted(this)
        }
    }

    companion object {
        suspend infix fun <F, A> Monad.Core<F>.`do`(f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            invoke(this, f)

        suspend operator fun <F, A> invoke(monad: Monad.Core<F>, f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            Comprehension<F, A>(monad).let { comprehension ->
                val block: suspend Comprehension<F, A>.() -> App<F, A> = {
                    try {
                        returns(comprehension.f())
                    } catch (completed: ComprehensionCompleted) {
                        completed.get()
                    }
                }
                block.startCoroutine(comprehension, comprehension.continuation)
                comprehension.continuation.getResult()
            }
    }

}