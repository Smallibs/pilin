package io.smallibs.pilin.control

import io.smallibs.pilin.core.execute
import io.smallibs.pilin.type.App
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.*

class Comprehension<F, A>(private val m: Monad.Core<F>) : Monad.Core<F> by m, Continuation<App<F, A>> {

    private lateinit var ma: App<F, A>

    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<App<F, A>>) {
        ma = result.getOrThrow() // TODO
    }

    suspend fun <B> App<F, B>.exec(): B =
        currentCoroutineContext().let { coroutineContext ->
            suspendCoroutine { cont ->
                execute(coroutineContext) {
                    ma = m.bind<B, A> { a ->
                        cont.resume(a)
                        ma
                    }(this)
                }
            }
        }

    companion object {
        suspend fun <F, A> run(m: Monad.Core<F>, f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            Comprehension<F, A>(m).let { comprehension ->
                val callback: suspend Comprehension<F, *>.() -> App<F, A> = { returns(comprehension.f()) }
                callback.startCoroutine(comprehension, comprehension)
                comprehension.ma
            }
    }

}