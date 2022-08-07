package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl : Functor.API<ContinuationK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<ContinuationK, A>, App<ContinuationK, B>> =
            { ma ->
                object : Continuation<B> {
                    override suspend fun <O> invoke(k: Fun<B, O>): O = ma(f then k)
                }
            }
    }

    fun functor(): Functor.API<ContinuationK> = FunctorImpl()
}