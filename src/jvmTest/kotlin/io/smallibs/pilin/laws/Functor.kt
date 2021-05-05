package io.smallibs.pilin.laws

import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.core.Standard.Infix.compose
import io.smallibs.pilin.core.Standard.id
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {

    suspend fun <F, A> Functor.API<F>.`map id = id`(
        x: App<F, A>,
    ): Boolean =
        with(this.infix) {
            val id: Fun<A, A> = Standard::id
            id map (x) == id(x)
        }

    suspend fun <F, A, B, C> Functor.API<F>.`map (f compose g) = map f compose map g`(
        f: Fun<B, C>,
        g: Fun<A, B>,
        x: App<F, A>,
    ): Boolean =
        with(this.infix) {
            (f compose g) map x == (map(f) compose map(g))(x)
        }


}