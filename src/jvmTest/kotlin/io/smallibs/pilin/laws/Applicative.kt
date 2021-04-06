package io.smallibs.pilin.laws

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.core.Compose
import io.smallibs.pilin.core.Fun
import io.smallibs.pilin.core.Fun.id
import io.smallibs.pilin.module.open
import io.smallibs.pilin.type.App

object Applicative {

    suspend fun <F, A, B> `map f x = apply (pure f) x`(
        applicative: Applicative.API<F>,
        f: suspend (A) -> B,
        x: App<F, A>
    ) {
        open(applicative) {
            assert(map(f)(x) == apply(pure(f))(x))
        }
    }

    suspend fun <F, A> `(pure id) apply v = v`(
        applicative: Applicative.API<F>,
        x: App<F, A>
    ) {
        open(applicative) {
            assert(apply<A, A>(pure { i -> id(i) })(x) == x)
        }
    }

    suspend fun <F, A, B> `apply (pure f) (pure x) = pure (f x)`(
        applicative: Applicative.API<F>,
        f: suspend (A) -> B,
        x: A
    ) {
        open(applicative) {
            assert(apply(pure(f))(pure(x)) == pure(f(x)))
        }
    }

    suspend fun <F, A, B> `apply f (pure x) = apply (pure ($ y)) u`(
        applicative: Applicative.API<F>,
        f: App<F, suspend (A) -> B>,
        x: A
    ) {
        open(applicative) {
            assert(apply(f)(pure(x)) == apply<suspend (A) -> B, B>(pure { f -> f(x) })(f))
        }
    }

    suspend fun <F, A, B, C> `apply f (apply g x) == apply (apply (apply (pure compose) f) g) x  `(
        applicative: Applicative.API<F>,
        f: App<F, suspend (B) -> C>,
        g: App<F, suspend (A) -> B>,
        x: App<F, A>,
    ) {
        open(applicative) {
            val c: Compose<A, B, C> = Fun::compose // o_O
            assert(apply(f)(apply(g)(x)) == apply(apply(apply(pure(c))(f))(g))(x))
        }
    }

}