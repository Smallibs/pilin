package io.smallibs.pilin.standard.freer.monad

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.standard.freer.monad.Freer.FreerK
import io.smallibs.pilin.standard.freer.monad.Freer.FreerK.Companion.fix
import io.smallibs.pilin.standard.freer.monad.Freer.Return
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl<F> : Applicative.API<FreerK<F>>, Applicative.WithPureAndApply<FreerK<F>> {
        override suspend fun <A> pure(a: A): App<FreerK<F>, A> = Return(a)

        override suspend fun <A, B> apply(mf: App<FreerK<F>, Fun<A, B>>): Fun<App<FreerK<F>, A>, App<FreerK<F>, B>> = {
            mf.fix.bind { f -> it.fix.map(f) }
        }
    }

    fun <F> applicative(): Applicative.API<FreerK<F>> = ApplicativeImpl()
}