package io.smallibs.pilin.standard.either

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.either.Either.TK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl<L> :
        Applicative.API<Either.TK<L>>,
        Applicative.WithPureAndApply<Either.TK<L>> {
        override suspend fun <R> pure(a: R): App<Either.TK<L>, R> = right(a)
        override suspend fun <A, B> apply(mf: App<Either.TK<L>, Fun<A, B>>): Fun<App<Either.TK<L>, A>, App<Either.TK<L>, B>> =
            { ma -> mf.fold(::left) { f -> ma.fold(::left) { a -> pure(f(a)) } } }
    }

    fun <L> applicative(): Applicative.API<Either.TK<L>> = ApplicativeImpl()
}