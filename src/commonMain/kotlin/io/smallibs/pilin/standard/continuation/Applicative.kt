package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.TK
import io.smallibs.pilin.standard.continuation.Continuation.TK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl<O> :
        Applicative.API<TK<O>>,
        Applicative.WithPureAndApply<TK<O>> {
        override suspend fun <I> pure(a: I): App<TK<O>, I> = continuation { k -> k(a) }
        override suspend fun <A, B> apply(mf: App<TK<O>, Fun<A, B>>): Fun<App<TK<O>, A>, App<TK<O>, B>> = { ma ->
            continuation { k -> mf { f -> ma(f then k) } }
        }
    }

    fun <L> applicative(): Applicative.API<TK<L>> = ApplicativeImpl()
}