package io.smallibs.pilin.standard.option

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.Companion.some
import io.smallibs.pilin.standard.option.Option.OptionK
import io.smallibs.pilin.standard.option.Option.OptionK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl : Functor.API<OptionK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<OptionK, A>, App<OptionK, B>> =
            { ma -> ma.fold(::none, f then ::some) }
    }

    val functor: Functor.API<OptionK> = FunctorImpl()
}