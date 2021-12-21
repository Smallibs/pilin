package io.smallibs.pilin.standard.option

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.Companion.some
import io.smallibs.pilin.standard.option.Option.OptionK
import io.smallibs.pilin.standard.option.Option.OptionK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl :
        Applicative.API<OptionK>,
        Applicative.WithPureAndApply<OptionK> {
        override suspend fun <A> pure(a: A): App<OptionK, A> = some(a)
        override suspend fun <A, B> apply(mf: App<OptionK, Fun<A, B>>): Fun<App<OptionK, A>, App<OptionK, B>> =
            { ma -> mf.fold(::none) { f -> ma.fold(::none, f then ::some) } }
    }

    val applicative: Applicative.API<OptionK> = ApplicativeImpl()
}