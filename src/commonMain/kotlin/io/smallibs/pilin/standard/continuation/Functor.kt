package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl<O> : Functor.API<ContinuationK<O>> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<ContinuationK<O>, A>, App<ContinuationK<O>, B>> =
            { ma -> continuation { b -> ma(f then b) } }
    }

    fun <O> functor(): Functor.API<ContinuationK<O>> = FunctorImpl()
}