package io.smallibs.pilin.standard.result

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.standard.result.Result.Companion.applicative
import io.smallibs.pilin.standard.result.Result.Companion.error
import io.smallibs.pilin.standard.result.Result.ResultK
import io.smallibs.pilin.standard.result.Result.ResultK.Companion.fold
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl<E>(applicative: Applicative.API<ResultK<E>>) : Monad.API<ResultK<E>>,
        Monad.WithReturnsMapAndJoin<ResultK<E>>, Monad.ViaApplicative<ResultK<E>>(applicative) {
        override suspend fun <A> join(mma: App<ResultK<E>, App<ResultK<E>, A>>): App<ResultK<E>, A> =
            mma.fold(::error, Standard::id)
    }

    fun <E> monad(): Monad.API<ResultK<E>> = MonadImpl(applicative())
}