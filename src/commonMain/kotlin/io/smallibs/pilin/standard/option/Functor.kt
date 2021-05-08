package io.smallibs.pilin.standard.option

import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.Companion.some
import io.smallibs.pilin.standard.option.Option.TK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl : Functor.API<Option.TK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<Option.TK, A>, App<Option.TK, B>> =
            { ma -> ma.fold(::none) { a -> some(f(a)) } }
    }

    val functor: Functor.API<Option.TK> = FunctorImpl()
}