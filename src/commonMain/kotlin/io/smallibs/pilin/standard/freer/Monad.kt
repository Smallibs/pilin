package io.smallibs.pilin.standard.freer

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.standard.freer.Applicative.applicative
import io.smallibs.pilin.standard.freer.Freer.FreerK
import io.smallibs.pilin.standard.freer.Freer.FreerK.Companion.fix
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Monad {
    private class MonadImpl<F> : Monad.API<FreerK<F>>, Monad.WithReturnsAndBind<FreerK<F>>,
        Monad.ViaApplicative<FreerK<F>>(applicative()) {

        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<FreerK<F>, A>, App<FreerK<F>, B>> = { it.fix.map(f) }

        override suspend fun <A, B> bind(f: Fun<A, App<FreerK<F>, B>>): Fun<App<FreerK<F>, A>, App<FreerK<F>, B>> =
            { it.fix.bind(f) }
    }

    fun <F> monad(): Monad.API<FreerK<F>> = MonadImpl()
}