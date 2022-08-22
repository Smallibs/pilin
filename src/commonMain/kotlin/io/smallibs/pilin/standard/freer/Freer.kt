package io.smallibs.pilin.standard.freer

import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.freer.Freer.FreerK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

sealed interface Freer<F, A> : App<FreerK<F>, A> {

    suspend fun <B> map(f: Fun<A, B>): Freer<F, B> = bind { Return(f(it)) }
    suspend fun <B> bind(f: Fun<A, Freer<F, B>>): Freer<F, B>

    interface Handler<F, A> {
        suspend fun <B> handle(f: Fun<B, A>): Fun<App<F, B>, A>
    }

    suspend fun run(f: Handler<F, A>): A

    data class Return<F, A>(val value: A) : Freer<F, A> {
        override suspend fun <B> bind(f: Fun<A, Freer<F, B>>) = f(value)
        override suspend fun run(f: Handler<F, A>): A = value
    }

    data class Bind<F, I, A>(val intermediate: App<F, I>, val continuation: Fun<I, Freer<F, A>>) : Freer<F, A> {
        override suspend fun <B> bind(f: Fun<A, Freer<F, B>>) = Bind(intermediate, continuation.then { it.bind(f) })
        override suspend fun run(f: Handler<F, A>): A = f.handle { x: I -> continuation(x).run(f) }(intermediate)
    }

    class FreerK<F> private constructor() {
        companion object {
            val <F, A> App<FreerK<F>, A>.fix: Freer<F, A> get() = this as Freer<F, A>
        }
    }

    companion object {
        suspend fun <F, A> perform(f: App<F, A>): Freer<F, A> = Bind(f) { Return(it) }

        fun <F> functor() = Functor.functor<F>()
        fun <F> applicative() = Applicative.applicative<F>()
        fun <F> monad() = Monad.monad<F>()
        fun <F> selective() = Selective.selective<F>()
    }

}