package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.identity.Identity.IdentityK
import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl : Applicative.API<IdentityK>, Applicative.WithPureAndApply<IdentityK> {
        override suspend fun <A> pure(a: A): App<IdentityK, A> = Identity(a)

        override suspend fun <A, B> apply(mf: App<IdentityK, Fun<A, B>>): Fun<App<IdentityK, A>, App<IdentityK, B>> =
            { ma -> mf.fold { f -> ma.fold(f then ::pure) } }
    }

    val applicative: Applicative.API<IdentityK> = ApplicativeImpl()
}