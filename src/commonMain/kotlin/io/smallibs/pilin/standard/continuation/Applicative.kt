package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl<O> :
        Applicative.API<ContinuationK<O>>,
        Applicative.WithPureAndApply<ContinuationK<O>> {
        override suspend fun <I> pure(a: I): App<ContinuationK<O>, I> = continuation { k -> k(a) }
        override suspend fun <A, B> apply(mf: App<ContinuationK<O>, Fun<A, B>>): Fun<App<ContinuationK<O>, A>, App<ContinuationK<O>, B>> =
            { ma ->
                continuation { k -> mf { f -> ma(f then k) } }
            }
    }

    fun <L> applicative(): Applicative.API<ContinuationK<L>> = ApplicativeImpl()
}