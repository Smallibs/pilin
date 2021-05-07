package io.smallibs.pilin.extension

import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.core.execute
import io.smallibs.pilin.type.App
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.*

class Comprehension<F, A>(private val m: Monad.Core<F>) : Monad.Core<F> by m {

    private class ComprehensionContinuation<F, A> : Continuation<App<F, A>> {
        // Cannot store a Result (inlined class)
        private var ma: Any? = null

        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        fun <B> intermediate(result: Result<App<F, B>>) {
            ma = result
        }

        override fun resumeWith(result: Result<App<F, A>>) {
            ma = result
        }

        @Suppress("UNCHECKED_CAST")
        fun get(): App<F, A> =
            (ma as Result<*>).getOrThrow() as App<F, A>
    }

    private val completion = ComprehensionContinuation<F, A>()

    suspend operator fun <B> App<F, B>.component1(): B =
        this.bind()

    suspend fun <B> App<F, B>.bind(): B =
        currentCoroutineContext().let { coroutineContext ->
            suspendCoroutine { cont ->
                execute(coroutineContext) {
                    completion.intermediate(Result.success(m.bind<B, B> { b ->
                        cont.resume(b)
                        pure(b)
                    }(this)))
                }
            }
        }

    companion object {
        suspend infix fun <F, A> Monad.Core<F>.`do`(f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            invoke(this, f)

        suspend operator fun <F, A> invoke(m: Monad.Core<F>, f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            Comprehension<F, A>(m).let { comprehension ->
                val callback: suspend Comprehension<F, *>.() -> App<F, A> = { returns(comprehension.f()) }
                callback.startCoroutine(comprehension, comprehension.completion).let {
                    comprehension.completion.get()
                }
            }
    }

}