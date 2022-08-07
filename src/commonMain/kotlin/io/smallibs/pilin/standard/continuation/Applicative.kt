package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl : Applicative.API<ContinuationK>, Applicative.WithPureAndApply<ContinuationK> {
        override suspend fun <I> pure(a: I): App<ContinuationK, I> = object : Continuation<I> {
            override suspend fun <O> invoke(k: Fun<I, O>): O = k(a)
        }

        override suspend fun <A, B> apply(mf: App<ContinuationK, Fun<A, B>>): Fun<App<ContinuationK, A>, App<ContinuationK, B>> =
            { ma ->
                object : Continuation<B> {
                    override suspend fun <O> invoke(k: Fun<B, O>): O = mf { f -> ma(f then k) }
                }
            }
    }

    fun applicative(): Applicative.API<ContinuationK> = ApplicativeImpl()
}