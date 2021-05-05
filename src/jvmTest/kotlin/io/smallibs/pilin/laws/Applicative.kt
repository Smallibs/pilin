package io.smallibs.pilin.laws

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.core.Compose
import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {

    suspend fun <F, A, B> Applicative.API<F>.`map f x = apply (pure f) x`(
        f: Fun<A, B>,
        x: App<F, A>,
    ): Boolean =
        with(this.infix) {
            f map x == pure(f) apply x
        }

    suspend fun <F, A> Applicative.API<F>.`(pure id) apply v = v`(
        x: App<F, A>,
    ): Boolean =
        with(this.infix) {
            val id: Fun<A, A> = Standard::id
            pure(id) apply (x) == x
        }

    suspend fun <F, A, B> Applicative.API<F>.`apply (pure f) (pure x) = pure (f x)`(
        f: Fun<A, B>,
        x: A,
    ): Boolean =
        with(this.infix) {
            pure(f) apply pure(x) == pure(f(x))
        }

    suspend fun <F, A, B> Applicative.API<F>.`apply f (pure x) = apply (pure ($ y)) f`(
        f: App<F, Fun<A, B>>,
        x: A,
    ): Boolean =
        with(this.infix) {
            val g: Fun<Fun<A, B>, B> = { g -> g(x) }
            f apply pure(x) == pure(g) apply (f)
        }

    suspend fun <F, A, B, C> Applicative.API<F>.`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x  `(
        f: App<F, Fun<B, C>>,
        g: App<F, Fun<A, B>>,
        x: App<F, A>,
    ): Boolean =
        with(this.infix) {
            val c: Compose<A, B, C> = Standard::compose
            f apply (g apply x) == pure(c) apply f apply g apply x
        }
}