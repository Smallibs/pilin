package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.standard.identity.Identity.Companion.id
import io.smallibs.pilin.standard.identity.Identity.TK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl : Functor.API<Identity.TK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<Identity.TK, A>, App<Identity.TK, B>> =
            { ma -> id(ma.fold(f)) }

    }

    val functor: Functor.API<Identity.TK> = FunctorImpl()
}