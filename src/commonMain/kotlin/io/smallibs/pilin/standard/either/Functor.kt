package io.smallibs.pilin.standard.either

import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.either.Either.TK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl<L> : Functor.API<Either.TK<L>> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<Either.TK<L>, A>, App<Either.TK<L>, B>> =
            { ma -> ma.fold(::left) { a -> right(f(a)) } }
    }

    fun <L> functor(): Functor.API<Either.TK<L>> = FunctorImpl()
}