package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.abstractions.Applicative.API
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.standard.continuation.Applicative.applicative
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Monad {
    private class MonadImpl(applicative: API<ContinuationK>) :
        Monad.API<ContinuationK>,
        Monad.WithReturnsMapAndJoin<ContinuationK>,
        Monad.ViaApplicative<ContinuationK>(applicative) {

        override suspend fun <A> join(mma: App<ContinuationK, App<ContinuationK, A>>): App<ContinuationK, A> =
            object : Continuation<A> {
                override suspend fun <O> invoke(k: Fun<A, O>): O = mma { it(k) }
            }

    }

    fun monad(): Monad.API<ContinuationK> = MonadImpl(applicative())
}