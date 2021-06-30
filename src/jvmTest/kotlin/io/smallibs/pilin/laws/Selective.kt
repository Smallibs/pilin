package io.smallibs.pilin.laws

import io.smallibs.pilin.control.Selective
import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.standard.either.Either.EitherK
import io.smallibs.pilin.standard.either.Either.EitherK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Selective {

    suspend fun <F, A> Selective.API<F>.`x select (pure id) = fold(id)(id) map x`(
        x: App<EitherK<A>, A>,
    ): Boolean =
        with(this.infix) {
            pure(x) select pure(Standard::id) == fold<A, A, A>(Standard::id)(Standard::id) map pure(x)
        }

    suspend fun <F, A, B> Selective.API<F>.`pure(x) select (y discardLeft z) = (pure(x) select y) discardLeft ((pure(x) select z)`(
        f: Fun<A, B>,
        g: Fun<A, B>,
        x: App<EitherK<A>, B>,
    ): Boolean =
        with(this.infix) {
            val y = pure(f)
            val z = pure(g)
            pure(x) select (y discardLeft z) == (pure(x) select y) discardLeft (pure(x) select z)
        }
}