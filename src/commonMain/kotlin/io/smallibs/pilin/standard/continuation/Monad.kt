package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.control.Applicative.API
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.continuation.Applicative.applicative
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl(applicative: API<ContinuationK>) :
        Monad.API<ContinuationK>,
        Monad.WithReturnsMapAndJoin<ContinuationK>,
        Monad.ViaApplicative<ContinuationK>(applicative) {
        override suspend fun <A> join(mma: App<ContinuationK, App<ContinuationK, A>>): App<ContinuationK, A> =
            continuation<A, Any> { a -> mma { ma -> ma(a) } }
    }

    val monad: Monad.API<ContinuationK> = MonadImpl(applicative)
}