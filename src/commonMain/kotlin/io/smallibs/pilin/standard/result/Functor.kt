package io.smallibs.pilin.standard.result

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.standard.result.Result.Companion.failure
import io.smallibs.pilin.standard.result.Result.Companion.success
import io.smallibs.pilin.standard.result.Result.ResultK
import io.smallibs.pilin.standard.result.Result.ResultK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl<E> : Functor.API<ResultK<E>> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<ResultK<E>, A>, App<ResultK<E>, B>> = { ma ->
            ma.fold({ failure(it) }) { success(f(it)) }
        }
    }

    fun <E> functor(): Functor.API<ResultK<E>> = FunctorImpl()
}