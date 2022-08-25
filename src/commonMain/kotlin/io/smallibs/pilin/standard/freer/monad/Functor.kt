package io.smallibs.pilin.standard.freer.monad

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.standard.freer.monad.Freer.FreerK
import io.smallibs.pilin.standard.freer.monad.Freer.FreerK.Companion.fix
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl<F> : Functor.API<FreerK<F>> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<FreerK<F>, A>, App<FreerK<F>, B>> =
            { it.fix.map(f) }
    }

    fun <F> functor(): Functor.API<FreerK<F>> = FunctorImpl<F>()
}