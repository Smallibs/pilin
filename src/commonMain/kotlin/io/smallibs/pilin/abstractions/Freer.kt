package io.smallibs.pilin.abstractions

import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

sealed interface Freer<F, A> : App<Freer.TK<F>, A> {

    interface Handler<F, A> {
        suspend fun <B> handle(f: Fun<B, A>): Fun<App<F, B>, A>
    }

    suspend fun run(f: Handler<F, A>): A
    suspend fun <B> map(f: Fun<A, B>): Freer<F, B> = bind { Return(f(it)) }

    suspend fun <B> bind(f: Fun<A, Freer<F, B>>): Freer<F, B>

    data class Return<F, A>(val value: A) : Freer<F, A> {
        override suspend fun <B> bind(f: Fun<A, Freer<F, B>>) = f(value)
        override suspend fun run(f: Handler<F, A>): A = value
    }

    data class Bind<F, I, A>(val intermediate: App<F, I>, val continuation: Fun<I, Freer<F, A>>) : Freer<F, A> {
        override suspend fun <B> bind(f: Fun<A, Freer<F, B>>) = Bind(intermediate, continuation.then { it.bind(f) })
        override suspend fun run(f: Handler<F, A>): A = f.handle { x: I -> continuation(x).run(f) }(intermediate)
    }

    class TK<F> private constructor() {
        val <A> App<TK<F>, A>.fix: Freer<F, A> get() = this as Freer<F, A>
    }

    companion object {
        suspend fun <F, A> perform(f: App<F, A>): Freer<F, A> = Bind(f) { Return(it) }
    }

}