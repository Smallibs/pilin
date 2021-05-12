package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.control.Applicative.API
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.continuation.Applicative.applicative
import io.smallibs.pilin.standard.continuation.Continuation.TK
import io.smallibs.pilin.standard.continuation.Continuation.TK.Companion.invoke
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl<O>(applicative: API<TK<O>>) :
        Monad.API<TK<O>>,
        Monad.WithReturnsMapAndJoin<TK<O>>,
        Monad.ViaApplicative<TK<O>>(applicative) {
        override suspend fun <A> join(mma: App<TK<O>, App<TK<O>, A>>): App<TK<O>, A> =
            Continuation { a -> mma { ma -> ma(a) } }
    }

    fun <O> monad(): Monad.API<TK<O>> = MonadImpl(applicative())
}