package io.smallibs.pilin.standard.state

import io.smallibs.pilin.abstractions.Transformer
import io.smallibs.pilin.standard.state.State.StateK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
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

    class OverMonad<F>(private val inner: Monad_Core<F>) {
        suspend fun <S, A> eval(ms: State<F, S, A>): Fun<S, App<F, A>> = { s ->
            inner.map { (a, _): Pair<A, S> -> a }(ms.run(s))
        }

        suspend fun <S, A> exec(ms: State<F, S, A>): Fun<S, App<F, S>> =
            { s -> inner.map { (_, s): Pair<A, S> -> s }(ms.run(s)) }

        suspend fun <S, A> run(ms: State<F, S, A>): Fun<S, App<F, Pair<A, S>>> = { ms.run(it) }

        suspend fun <S, A> state(f: Fun<S, Pair<A, S>>): State<F, S, A> = State { s -> inner.returns(f(s)) }

        @Suppress("NAME_SHADOWING")
        suspend fun <S> get(): State<F, S, S> = State { s -> (state<S, S> { s -> s to s }).run(s) }

        suspend fun <S> set(): State<F, S, Unit> = State { s -> (state<S, Unit> { _ -> Unit to s }).run(s) }

        @Suppress("NAME_SHADOWING")
        suspend fun <S> modify(f: Fun<S, S>): State<F, S, Unit> =
            State { s -> (state<S, Unit> { s -> Unit to f(s) }).run(s) }

        @Suppress("NAME_SHADOWING")
        suspend fun <S, A> gets(f: Fun<S, A>): State<F, S, A> = State { s -> (state<S, A> { s -> f(s) to s }).run(s) }

        fun <S> transformer(): Transformer<F, StateK<F, S>> {
            return object : Transformer<F, StateK<F, S>> {
                override suspend fun <A> upper(ma: App<F, A>): App<StateK<F, S>, A> {
                    return State { s -> inner.bind { a: A -> inner.returns(a to s) }(ma) }
                }
            }
        }

        fun <S> functor() = Functor.functor<F, S>(inner)
        fun <S> applicative() = Applicative.applicative<F, S>(inner)
        fun <S> monad() = Monad.monad<F, S>(inner)
        fun <S> selective() = Selective.selective<F, S>(inner)
    }

}