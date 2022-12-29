package io.smallibs.pilin.standard.`try`

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.standard.`try`.Try.Companion.failure
import io.smallibs.pilin.standard.`try`.Try.Companion.success
import io.smallibs.pilin.standard.`try`.Try.TryK
import io.smallibs.pilin.standard.`try`.Try.TryK.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl : Applicative.API<TryK>, Applicative.WithPureAndApply<TryK> {
        override suspend fun <A> pure(a: A): App<TryK, A> = success(a)

        override suspend fun <A, B> apply(mf: App<TryK, Fun<A, B>>): Fun<App<TryK, A>, App<TryK, B>> =
            { ma -> mf.fold(::failure) { f -> ma.fold(::failure) { success(f(it)) } } }
    }

    val applicative: Applicative.API<TryK> = ApplicativeImpl()
}