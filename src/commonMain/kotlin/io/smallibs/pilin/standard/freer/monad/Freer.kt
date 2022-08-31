package io.smallibs.pilin.standard.freer.monad

import io.smallibs.pilin.abstractions.Monad.API
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.freer.monad.Freer.FreerK
import io.smallibs.pilin.standard.freer.monad.Freer.FreerK.Companion.fix
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

sealed interface Freer<F, A> : App<FreerK<F>, A> {

    class FreerK<F> private constructor() {
        companion object {
            val <F, A> App<FreerK<F>, A>.fix: Freer<F, A> get() = this as Freer<F, A>
        }
    }

    suspend fun <B> map(f: Fun<A, B>): App<FreerK<F>, B> = bind { Return(f(it)) }

    suspend fun <B> bind(f: Fun<A, App<FreerK<F>, B>>): App<FreerK<F>, B>

    interface Handler<F, A> {
        suspend fun <B> handle(f: Fun<B, A>): Fun<App<F, B>, A>
    }

    suspend fun run(f: Handler<F, A>): A

    data class Return<F, A>(val value: A) : Freer<F, A> {
        override suspend fun <B> bind(f: Fun<A, App<FreerK<F>, B>>) = f(value)
        override suspend fun run(f: Handler<F, A>): A = value
    }

    data class Bind<F, I, A>(val intermediate: App<F, I>, val continuation: Fun<I, App<FreerK<F>, A>>) : Freer<F, A> {
        override suspend fun <B> bind(f: Fun<A, App<FreerK<F>, B>>) =
            Bind(intermediate, continuation.then { it.fix.bind(f) })

        override suspend fun run(f: Handler<F, A>): A = f.handle { x: I -> continuation(x).fix.run(f) }(intermediate)
    }

    open class Over<F>(val api: API<FreerK<F>> = Monad.monad()) : API<FreerK<F>> by api {
        fun <F, A> perform(f: App<F, A>): Freer<F, A> = Bind(f) { Return(it) }
        suspend fun <F, A> run(f: Handler<F, A>, ma: App<FreerK<F>, A>): A = ma.fix.run(f)

        fun <F> functor() = Functor.functor<F>()
        fun <F> applicative() = Applicative.applicative<F>()
        fun <F> monad() = api
        fun <F> selective() = Selective.selective<F>()
    }

}