package io.smallibs.pilin.laws

import io.smallibs.pilin.control.Selective
import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.either.Either.TK.Companion.fold
import io.smallibs.pilin.type.App

object Selective {

    suspend fun <F, A> Selective.API<F>.`x select (pure id) = fold(id)(id) map x`(
        x: App<F, App<Either.TK<A>, A>>,
    ): Boolean =
        with(this.infix) {
            x select pure(Standard::id) == fold<A, A, A>(Standard::id)(Standard::id) map x
        }

}