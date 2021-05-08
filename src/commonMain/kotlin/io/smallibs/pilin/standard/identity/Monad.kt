package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.identity.Identity.TK.Companion.fold
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl(applicative: io.smallibs.pilin.control.Applicative.API<Identity.TK>) :
        Monad.API<Identity.TK>,
        Monad.WithReturnsMapAndJoin<Identity.TK>,
        Monad.ViaApplicative<Identity.TK>(applicative) {
        override suspend fun <A> join(mma: App<Identity.TK, App<Identity.TK, A>>): App<Identity.TK, A> =
            mma.fold { it }
    }

    val monad: Monad.API<Identity.TK> = MonadImpl(Applicative.applicative)
}