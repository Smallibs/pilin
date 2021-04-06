package io.smallibs.pilin.laws

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.core.Compose
import io.smallibs.pilin.core.Fun
import io.smallibs.pilin.core.Fun.id
import io.smallibs.pilin.module.open
import io.smallibs.pilin.type.App

object Applicative {

    suspend fun <F, A, B> Applicative.API<F>.`map f x = apply (pure f) x`(
        f: suspend (A) -> B,
        x: App<F, A>
    ): Boolean =
        open(this.infix) {
            f map x == pure(f) apply x
        }

    suspend fun <F, A> Applicative.API<F>.`(pure id) apply v = v`(
        x: App<F, A>
    ): Boolean =
        open(this.infix) {
            pure<suspend (A) -> A> { i -> id(i) } apply (x) == x
        }

    suspend fun <F, A, B> Applicative.API<F>.`apply (pure f) (pure x) = pure (f x)`(
        f: suspend (A) -> B,
        x: A
    ): Boolean =
        open(this.infix) {
            pure(f) apply pure(x) == pure(f(x))
        }

    suspend fun <F, A, B> Applicative.API<F>.`apply f (pure x) = apply (pure ($ y)) f`(
        f: App<F, suspend (A) -> B>,
        x: A
    ): Boolean =
        open(this.infix) {
            val g : suspend (suspend (A) -> B) -> B = { g -> g(x) }
            f apply pure(x) == pure(g) apply (f)
        }

    suspend fun <F, A, B, C> Applicative.API<F>.`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x  `(
        f: App<F, suspend (B) -> C>,
        g: App<F, suspend (A) -> B>,
        x: App<F, A>,
    ): Boolean =
        open(this.infix) {
            val c: Compose<A, B, C> = Fun::compose // o_O
            f apply (g apply x) == pure(c) apply f apply g apply x
        }
}