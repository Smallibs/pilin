package io.smallibs.pilin.standard.state

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.standard.state.State.StateK
import io.smallibs.pilin.standard.state.State.StateK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl<F, S>(private val inner: Functor.Core<F>) : Functor.API<StateK<F, S>> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<StateK<F, S>, A>, App<StateK<F, S>, B>> =
            { ma -> State { s -> inner.map { (a, s): Pair<A, S> -> f(a) to s }(ma.invoke(s)) } }
    }

    fun <F, E> functor(inner: Functor.Core<F>): Functor.API<StateK<F, E>> = FunctorImpl(inner)
}