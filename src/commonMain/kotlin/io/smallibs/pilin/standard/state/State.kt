package io.smallibs.pilin.standard.state

import io.smallibs.pilin.abstractions.Transformer
import io.smallibs.pilin.standard.state.State.StateK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.abstractions.Functor.Core as Functor_Core
import io.smallibs.pilin.abstractions.Monad.Core as Monad_Core

data class State<F, S, A>(val run: suspend (S) -> App<F, Pair<A, S>>) : App<StateK<F, S>, A> {

    // This code can be automatically generated
    class StateK<F, S> private constructor() {
        companion object {
            private val <F, S, A> App<StateK<F, S>, A>.fix: State<F, S, A>
                get() = this as State<F, S, A>

            operator fun <F, S, A> App<StateK<F, S>, A>.invoke(e: S): App<F, Pair<A, S>> = this.fix(e)
        }
    }

    class OverMonad<F, S>(val monad: Monad_Core<F>) : Transformer<F, StateK<F, S>> {
        suspend fun <A> eval(ms: State<F, S, A>): Fun<S, App<F, A>> = { s ->
            monad.map { (a, _): Pair<A, S> -> a }(ms.run(s))
        }

        suspend fun <A> exec(ms: State<F, S, A>): Fun<S, App<F, S>> =
            { s -> monad.map { (_, s): Pair<A, S> -> s }(ms.run(s)) }

        suspend fun <A> run(ms: State<F, S, A>): Fun<S, App<F, Pair<A, S>>> = { ms.run(it) }

        suspend fun <A> state(f: Fun<S, Pair<A, S>>): State<F, S, A> = State { s -> monad.returns(f(s)) }

        @Suppress("NAME_SHADOWING")
        suspend fun get(): State<F, S, S> = State { s -> (state { s -> s to s }).run(s) }

        suspend fun set(): State<F, S, Unit> = State { s -> (state { _ -> Unit to s }).run(s) }

        @Suppress("NAME_SHADOWING")
        suspend fun modify(f: Fun<S, S>): State<F, S, Unit> = State { s -> (state { s -> Unit to f(s) }).run(s) }

        @Suppress("NAME_SHADOWING")
        suspend fun <A> gets(f: Fun<S, A>): State<F, S, A> = State { s -> (state { s -> f(s) to s }).run(s) }

        override suspend fun <A> upper(ma: App<F, A>): App<StateK<F, S>, A> {
            return State { s -> monad.bind { a: A -> monad.returns(a to s) }(ma) }
        }
    }

    companion object {
        fun <F, E> functor(f: Functor_Core<F>) = Functor.functor<F, E>(f)
        fun <F, E> applicative(a: Monad_Core<F>) = Applicative.applicative<F, E>(a)
        fun <F, E> monad(m: Monad_Core<F>) = Monad.monad<F, E>(m)
        fun <F, E> selective(m: Monad_Core<F>) = Selective.selective<F, E>(m)
    }
}