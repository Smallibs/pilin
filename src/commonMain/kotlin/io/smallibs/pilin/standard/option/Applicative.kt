package io.smallibs.pilin.standard.option

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.TK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl :
        Applicative.API<Option.TK>,
        Applicative.WithPureAndApply<Option.TK> {
        override suspend fun <A> pure(a: A): App<Option.TK, A> = Option.some(a)
        override suspend fun <A, B> apply(mf: App<Option.TK, Fun<A, B>>): Fun<App<Option.TK, A>, App<Option.TK, B>> =
            { ma -> mf.fold(::none) { f -> ma.fold(::none) { a -> pure(f(a)) } } }
    }

    val applicative: Applicative.API<Option.TK> = ApplicativeImpl()
}