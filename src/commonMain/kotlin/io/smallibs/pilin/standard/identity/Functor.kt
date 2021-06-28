package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.standard.identity.Identity.Companion.id
import io.smallibs.pilin.standard.identity.Identity.IdentityK
import io.smallibs.pilin.standard.identity.Identity.IdentityK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl : Functor.API<IdentityK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<IdentityK, A>, App<IdentityK, B>> =
            { ma -> id(ma.fold(f)) }

    }

    val functor: Functor.API<IdentityK> = FunctorImpl()
}