package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.core.Standard.id
import io.smallibs.pilin.standard.identity.Applicative.applicative
import io.smallibs.pilin.standard.identity.Identity.IdentityK
import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl(applicative: Applicative.API<IdentityK>) :
        Monad.API<IdentityK>, Monad.WithReturnsMapAndJoin<IdentityK>, Monad.ViaApplicative<IdentityK>(applicative) {
        override suspend fun <A> join(mma: App<IdentityK, App<IdentityK, A>>): App<IdentityK, A> = mma.fold(::id)
    }

    val monad: Monad.API<IdentityK> = MonadImpl(applicative)
}