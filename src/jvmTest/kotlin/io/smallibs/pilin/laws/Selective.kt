package io.smallibs.pilin.laws

import io.smallibs.pilin.control.Selective
import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.either.Either.TK.Companion.fold
import io.smallibs.pilin.type.App

object Selective {

    suspend fun <F, A> Selective.API<F>.`x select (pure id) = fold(id)(id) map x`(
        x: App<Either.TK<A>, A>,
    ): Boolean =
        with(this.infix) {
            pure(x) select pure(Standard::id) == fold<A, A, A>(Standard::id)(Standard::id) map pure(x)
        }
}