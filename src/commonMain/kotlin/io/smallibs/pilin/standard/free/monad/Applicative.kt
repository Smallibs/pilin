package io.smallibs.pilin.standard.free.monad

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.standard.free.monad.Free.Bind
import io.smallibs.pilin.standard.free.monad.Free.FreeK
import io.smallibs.pilin.standard.free.monad.Free.FreeK.Companion.fold
import io.smallibs.pilin.standard.free.monad.Free.Return
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl<F>(val inner: Functor.Core<F>) : Applicative.API<FreeK<F>>,
        Applicative.WithPureAndApply<FreeK<F>> {
        override suspend fun <A> pure(a: A): App<FreeK<F>, A> = Return(a)

        override suspend fun <A, B> apply(mf: App<FreeK<F>, Fun<A, B>>): Fun<App<FreeK<F>, A>, App<FreeK<F>, B>> =
            { ma ->
                mf.fold({ f -> map(f)(ma) }, {
                    Bind(inner.map() { f: App<FreeK<F>, Fun<A, B>> -> apply(f)(ma) }(it))
                })
            }

    }

    fun <F> applicative(inner: Functor.Core<F>): Applicative.API<FreeK<F>> = ApplicativeImpl(inner)
}