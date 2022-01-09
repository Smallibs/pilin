package io.smallibs.pilin.laws

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.core.Compose
import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {

    suspend fun <F, A, B> Applicative.API<F>.`map f x = apply (pure f) x`(
        f: Fun<A, B>,
        x: App<F, A>,
        equatable: Equatable<App<F, B>> = Equatable.default(),
    ): Boolean =
        with(this.infix) {
            with(equatable) {
                f map x isEqualTo (pure(f) apply x)
            }
        }

    suspend fun <F, A> Applicative.API<F>.`(pure id) apply v = v`(
        x: App<F, A>,
        equatable: Equatable<App<F, A>> = Equatable.default(),
    ): Boolean =
        with(this.infix) {
            with(equatable) {
                pure(Standard.mkId<A>()) apply (x) isEqualTo x
            }
        }

    suspend fun <F, A, B> Applicative.API<F>.`apply (pure f) (pure x) = pure (f x)`(
        f: Fun<A, B>,
        x: A,
        equatable: Equatable<App<F, B>> = Equatable.default(),
    ): Boolean =
        with(this.infix) {
            with(equatable) {
                pure(f) apply pure(x) isEqualTo pure(f(x))
            }
        }

    suspend fun <F, A, B> Applicative.API<F>.`apply f (pure x) = apply (pure ($ y)) f`(
        f: App<F, Fun<A, B>>,
        x: A,
        equatable: Equatable<App<F, B>> = Equatable.default(),
    ): Boolean =
        with(this.infix) {
            with(equatable) {
                val g: Fun<Fun<A, B>, B> = { g -> g(x) }
                f apply pure(x) isEqualTo (pure(g) apply (f))
            }
        }

    suspend fun <F, A, B, C> Applicative.API<F>.`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`(
        f: App<F, Fun<B, C>>,
        g: App<F, Fun<A, B>>,
        x: App<F, A>,
        equatable: Equatable<App<F, C>> = Equatable.default(),
    ): Boolean =
        with(this.infix) {
            with(equatable) {
                val c: Compose<A, B, C> = Standard::compose
                f apply (g apply x) isEqualTo (pure(c) apply f apply g apply x)
            }
        }
}