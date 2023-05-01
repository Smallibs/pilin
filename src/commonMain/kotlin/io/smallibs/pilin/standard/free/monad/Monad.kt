package io.smallibs.pilin.standard.free.monad

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.standard.free.monad.Applicative.applicative
import io.smallibs.pilin.standard.free.monad.Free.Bind
import io.smallibs.pilin.standard.free.monad.Free.FreeK
import io.smallibs.pilin.standard.free.monad.Free.FreeK.Companion.fold
import io.smallibs.pilin.standard.free.monad.Functor.functor
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Monad {
    private class MonadImpl<F>(val inner: Functor.Core<F>) : Monad.API<FreeK<F>>, Monad.WithReturnsAndBind<FreeK<F>>,
        Monad.ViaApplicative<FreeK<F>>(applicative(inner)) {

        private val functor = functor(inner)

        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<FreeK<F>, A>, App<FreeK<F>, B>> = functor.map(f)

        override suspend fun <A, B> bind(f: Fun<A, App<FreeK<F>, B>>): Fun<App<FreeK<F>, A>, App<FreeK<F>, B>> =
            { ma -> ma.fold(f) { Bind(inner.map(bind(f))(it)) } }
    }

    fun <F> monad(inner: Functor.Core<F>): Monad.API<FreeK<F>> = MonadImpl(inner)
}