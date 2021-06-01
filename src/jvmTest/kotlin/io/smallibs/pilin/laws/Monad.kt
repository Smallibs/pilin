package io.smallibs.pilin.laws

import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Monad {

    suspend fun <F, A, B> Monad.API<F>.`returns a bind h = h a`(
        f: Fun<A, App<F, B>>,
        a: A,
    ): Boolean =
        with(this.infix) {
            returns(a) bind f == f(a)
        }

    suspend fun <F, A> Monad.API<F>.`a bind returns = a`(
        a: App<F, A>,
    ): Boolean =
        with(this.infix) {
            a bind ::returns == a
        }

    suspend fun <F, A, B, C> Monad.API<F>.`(a bind f) bind g = a bind {x in f x bind g}`(
        f: Fun<A, App<F, B>>,
        g: Fun<B, App<F, C>>,
        a: App<F, A>,
    ): Boolean =
        with(this.infix) {
            (a bind f) bind g == a bind { x -> f(x) bind g }
        }

}