package io.smallibs.pilin.standard.state

import io.smallibs.pilin.abstractions.Transformer
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.IdentityK
import io.smallibs.pilin.standard.state.State.StateK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.abstractions.Monad.API as Monad_API
import io.smallibs.pilin.abstractions.Monad.Core as Monad_Core

data class State<F, S, A>(val run: suspend (S) -> App<F, Pair<A, S>>) : App<StateK<F, S>, A> {

    // This code can be automatically generated
    class StateK<F, S> private constructor() {
        companion object {
            private val <F, S, A> App<StateK<F, S>, A>.fix: State<F, S, A>
                get() = this as State<F, S, A>

            suspend operator fun <F, S, A> App<StateK<F, S>, A>.invoke(e: S): App<F, Pair<A, S>> = this.fix.run(e)
        }
    }

    open class OverMonad<F, S>(
        private val inner: Monad_Core<F>,
        private val api: Monad_API<StateK<F, S>> = Monad.monad(inner),
    ) : Monad_API<StateK<F, S>> by api {
        suspend fun <A> eval(ms: State<F, S, A>): Fun<S, App<F, A>> = { s ->
            inner.map { (a, _): Pair<A, S> -> a }(ms.run(s))
        }

        suspend fun <A> exec(ms: State<F, S, A>): Fun<S, App<F, S>> =
            { s -> inner.map { (_, s): Pair<A, S> -> s }(ms.run(s)) }

        suspend fun <A> run(ms: State<F, S, A>): Fun<S, App<F, Pair<A, S>>> = { ms.run(it) }

        suspend fun <A> state(f: Fun<S, Pair<A, S>>): State<F, S, A> = State { s -> inner.returns(f(s)) }

        @Suppress("NAME_SHADOWING")
        suspend fun get(): State<F, S, S> = state { s -> s to s }

        suspend fun set(): State<F, S, Unit> = State { s -> (state { _ -> Unit to s }).run(s) }

        @Suppress("NAME_SHADOWING")
        suspend fun modify(f: Fun<S, S>): State<F, S, Unit> = state { s -> Unit to f(s) }

        @Suppress("NAME_SHADOWING")
        suspend fun <A> gets(f: Fun<S, A>): State<F, S, A> = state { s -> f(s) to s }

        fun transformer(): Transformer<F, StateK<F, S>> {
            return object : Transformer<F, StateK<F, S>> {
                override suspend fun <A> upper(ma: App<F, A>): App<StateK<F, S>, A> {
                    return State { s -> inner.bind { a: A -> inner.returns(a to s) }(ma) }
                }
            }
        }

        fun <S> functor() = Functor.functor<F, S>(inner)
        fun <S> applicative() = Applicative.applicative<F, S>(inner)
        fun <S> monad() = api
        fun <S> selective() = Selective.selective<F, S>(inner)
    }

    class Over<S> : OverMonad<IdentityK, S>(Identity.monad)
}