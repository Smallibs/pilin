package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.continuation.Continuation.TK
import io.smallibs.pilin.standard.continuation.Continuation.TK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl<O> : Functor.API<TK<O>> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<TK<O>, A>, App<TK<O>, B>> = { ma ->
            Continuation { b -> ma(f then b) }
        }
    }

    fun <O> functor(): Functor.API<TK<O>> = FunctorImpl()
}