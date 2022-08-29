package io.smallibs.pilin.standard.result

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.standard.result.Result.Companion.failure
import io.smallibs.pilin.standard.result.Result.Companion.success
import io.smallibs.pilin.standard.result.Result.ResultK
import io.smallibs.pilin.standard.result.Result.ResultK.Companion.fold
import io.smallibs.pilin.standard.result.Result.Success
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl<E> : Applicative.API<ResultK<E>>,
        Applicative.WithPureAndApply<ResultK<E>> {
        override suspend fun <A> pure(a: A): App<ResultK<E>, A> = Success(a)

        override suspend fun <A, B> apply(mf: App<ResultK<E>, Fun<A, B>>): Fun<App<ResultK<E>, A>, App<ResultK<E>, B>> =
            { ma -> mf.fold({ e -> failure(e) }, { f -> ma.fold({ failure(it) }, { success(f(it)) }) }) }
    }

    fun <E> applicative(): Applicative.API<ResultK<E>> = ApplicativeImpl()
}