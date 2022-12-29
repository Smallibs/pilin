package io.smallibs.pilin.standard.`try`

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.core.Standard.id
import io.smallibs.pilin.standard.`try`.Applicative.applicative
import io.smallibs.pilin.standard.`try`.Try.Companion.failure
import io.smallibs.pilin.standard.`try`.Try.TryK
import io.smallibs.pilin.standard.`try`.Try.TryK.fold
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl(applicative: Applicative.API<TryK>) : Monad.API<TryK>,
        Monad.WithReturnsMapAndJoin<TryK>, Monad.ViaApplicative<TryK>(applicative) {
        override suspend fun <A> join(mma: App<TryK, App<TryK, A>>): App<TryK, A> = mma.fold(::failure, ::id)
    }

    val monad: Monad.API<TryK> = MonadImpl(applicative)
}