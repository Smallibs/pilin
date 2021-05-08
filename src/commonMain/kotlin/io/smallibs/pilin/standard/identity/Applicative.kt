package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.standard.identity.Identity.TK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl :
        Applicative.API<Identity.TK>,
        Applicative.WithPureAndApply<Identity.TK> {
        override suspend fun <A> pure(a: A): App<Identity.TK, A> = Identity(a)
        override suspend fun <A, B> apply(mf: App<Identity.TK, Fun<A, B>>): Fun<App<Identity.TK, A>, App<Identity.TK, B>> =
            { ma -> mf.fold { f -> ma.fold { a -> pure(f(a)) } } }
    }

    val applicative: Applicative.API<Identity.TK> = ApplicativeImpl()
}