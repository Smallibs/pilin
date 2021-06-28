package io.smallibs.pilin.standard.either

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.either.Either.EitherK
import io.smallibs.pilin.standard.either.Either.EitherK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl<L> :
        Applicative.API<EitherK<L>>,
        Applicative.WithPureAndApply<EitherK<L>> {
        override suspend fun <R> pure(a: R): App<EitherK<L>, R> = right(a)
        override suspend fun <A, B> apply(mf: App<EitherK<L>, Fun<A, B>>): Fun<App<EitherK<L>, A>, App<EitherK<L>, B>> =
            { ma -> mf.fold(::left) { f -> ma.fold(::left) { a -> pure(f(a)) } } }
    }

    fun <L> applicative(): Applicative.API<EitherK<L>> = ApplicativeImpl()
}