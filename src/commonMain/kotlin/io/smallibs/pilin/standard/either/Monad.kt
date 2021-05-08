package io.smallibs.pilin.standard.either

import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.TK.Companion.fold
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl<L>(applicative: io.smallibs.pilin.control.Applicative.API<Either.TK<L>>) :
        Monad.API<Either.TK<L>>,
        Monad.WithReturnsMapAndJoin<Either.TK<L>>,
        Monad.ViaApplicative<Either.TK<L>>(applicative) {
        override suspend fun <A> join(mma: App<Either.TK<L>, App<Either.TK<L>, A>>): App<Either.TK<L>, A> =
            mma.fold(::left) { it }
    }

    fun <L> monad(): Monad.API<Either.TK<L>> = MonadImpl(Applicative.applicative())
}