package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl :
        Applicative.API<ContinuationK>,
        Applicative.WithPureAndApply<ContinuationK> {
        override suspend fun <I> pure(a: I): App<ContinuationK, I> = continuation<I, Any> { k -> k(a) }
        override suspend fun <A, B> apply(mf: App<ContinuationK, Fun<A, B>>): Fun<App<ContinuationK, A>, App<ContinuationK, B>> =
            { ma -> continuation<B, Any> { k -> mf { f -> ma(f then k) } } }
    }

    val applicative: Applicative.API<ContinuationK> = ApplicativeImpl()
}