package io.smallibs.pilin.control

import io.smallibs.pilin.core.execute
import io.smallibs.pilin.type.App
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.*

class Comprehension<F, A>(private val m: Monad.Core<F>) : Monad.Core<F> by m, Continuation<App<F, A>> {

    private lateinit var ma: App<F, *>

    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<App<F, A>>) {
        ma = result.getOrThrow() // Should not throw a n Exception
    }

    suspend operator fun <B> App<F, B>.component1(): B =
        this.exec()

    suspend fun <B> App<F, B>.exec(): B =
        currentCoroutineContext().let { coroutineContext ->
            suspendCoroutine { cont ->
                execute(coroutineContext) {
                    ma = m.bind<B, B> { b ->
                        cont.resume(b)
                        pure(b)
                    }(this)
                }
            }
        }

    companion object {
        @Suppress("UNCHECKED_CAST")
        suspend fun <F, A> run(m: Monad.Core<F>, f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            Comprehension<F, A>(m).let { comprehension ->
                val callback: suspend Comprehension<F, *>.() -> App<F, A> = { returns(comprehension.f()) }
                callback.startCoroutine(comprehension, comprehension)
                comprehension.ma as App<F, A>
            }
    }

}