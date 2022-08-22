package io.smallibs.pilin.standard.state

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.standard.state.State.StateK
import io.smallibs.pilin.standard.state.State.StateK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl<F, S>(val inner: Monad.Core<F>) : Applicative.API<StateK<F, S>>,
        Applicative.WithPureAndApply<StateK<F, S>> {

        override suspend fun <A> pure(a: A): App<StateK<F, S>, A> = State { s -> inner.pure(a to s) }

        override suspend fun <A, B> apply(mf: App<StateK<F, S>, Fun<A, B>>): Fun<App<StateK<F, S>, A>, App<StateK<F, S>, B>> =
            { ms ->
                State { s ->
                    inner.bind { (f, s): Pair<Fun<A, B>, S> ->
                        inner.map { (a, s): Pair<A, S> -> f(a) to s }(ms(s))
                    }(mf.invoke(s))
                }
            }
    }

    fun <F, E> applicative(inner: Monad.Core<F>): Applicative.API<StateK<F, E>> = ApplicativeImpl(inner)
}