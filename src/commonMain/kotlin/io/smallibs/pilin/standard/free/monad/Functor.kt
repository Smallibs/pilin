package io.smallibs.pilin.standard.free.monad

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.standard.free.monad.Free.Bind
import io.smallibs.pilin.standard.free.monad.Free.FreeK
import io.smallibs.pilin.standard.free.monad.Free.FreeK.Companion.fold
import io.smallibs.pilin.standard.free.monad.Free.Return
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl<F>(val inner: Functor.Core<F>) : Functor.API<FreeK<F>> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<FreeK<F>, A>, App<FreeK<F>, B>> = { ma ->
            ma.fold({ Return(f(it)) }, { Bind(inner.map(map(f))(it)) })
        }
    }

    fun <F> functor(inner: Functor.Core<F>): Functor.API<FreeK<F>> = FunctorImpl<F>(inner)
}