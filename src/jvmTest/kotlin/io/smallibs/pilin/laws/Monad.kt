package io.smallibs.pilin.laws

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.core.Standard.With
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Monad {

    suspend fun <F, A, B> Monad.API<F>.`returns a bind h = h a`(
        f: Fun<A, App<F, B>>,
        a: A,
        equatable: Equatable<App<F, B>> = Equatable.default(),
    ): Boolean = With(this.infix, equatable) {
        {
            returns(a) bind f isEqualTo f(a)
        }
    }

    suspend fun <F, A> Monad.API<F>.`a bind returns = a`(
        a: App<F, A>,
        equatable: Equatable<App<F, A>> = Equatable.default(),
    ): Boolean = With(this.infix, equatable) {
        {
            a bind ::returns isEqualTo a
        }
    }

    suspend fun <F, A, B, C> Monad.API<F>.`(a bind f) bind g = a bind {x in f x bind g}`(
        f: Fun<A, App<F, B>>,
        g: Fun<B, App<F, C>>,
        a: App<F, A>,
        equatable: Equatable<App<F, C>> = Equatable.default(),
    ): Boolean = With(this.infix, equatable) {
        {
            (a bind f) bind g isEqualTo (a bind { x -> f(x) bind g })
        }
    }

}