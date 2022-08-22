package io.smallibs.pilin.standard.state

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.standard.state.Applicative.applicative
import io.smallibs.pilin.standard.state.Functor.functor
import io.smallibs.pilin.standard.state.State.StateK
import io.smallibs.pilin.standard.state.State.StateK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Monad {
    private class MonadImpl<F, S>(val inner: Monad.Core<F>) : Monad.API<StateK<F, S>>,
        Monad.WithReturnsAndBind<StateK<F, S>>, Monad.ViaApplicative<StateK<F, S>>(applicative(inner)) {

        val functor: Functor.Core<StateK<F, S>> = functor(inner)

        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<StateK<F, S>, A>, App<StateK<F, S>, B>> = functor.map(f)

        override suspend fun <A, B> bind(f: Fun<A, App<StateK<F, S>, B>>): Fun<App<StateK<F, S>, A>, App<StateK<F, S>, B>> =
            { ms -> State { s -> inner.bind { (a, s): Pair<A, S> -> f(a)(s) }(ms(s)) } }
    }

    fun <F, E> monad(inner: Monad.Core<F>): Monad.API<StateK<F, E>> = MonadImpl(inner)
}