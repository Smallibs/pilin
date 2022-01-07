package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.control.Applicative.API
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.continuation.Applicative.applicative
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl<O>(applicative: API<ContinuationK<O>>) :
        Monad.API<ContinuationK<O>>,
        Monad.WithReturnsMapAndJoin<ContinuationK<O>>,
        Monad.ViaApplicative<ContinuationK<O>>(applicative) {

        override suspend fun <A> join(mma: App<ContinuationK<O>, App<ContinuationK<O>, A>>): App<ContinuationK<O>, A> =
            continuation { a -> mma { ma -> ma(a) } }
        
    }

    fun <O> monad(): Monad.API<ContinuationK<O>> = MonadImpl(applicative())
}