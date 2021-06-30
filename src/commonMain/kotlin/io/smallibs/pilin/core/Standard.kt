package io.smallibs.pilin.core

import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.type.Fun2
import io.smallibs.pilin.type.Fun3

typealias Lambda<A, B> = Fun<A, B>
typealias Compose<A, B, C> = Lambda<Lambda<B, C>, Lambda<Lambda<A, B>, Lambda<A, C>>>

object Standard {

    suspend fun <A> id(a: A): A = a

    suspend fun <A, B> const(a: A): Fun<B, A> = { a }

    suspend fun <A, B, C> flip(f: Fun<A, Fun<B, C>>): Fun<B, Fun<A, C>> =
        { b -> { a -> f(a)(b) } }

    suspend infix fun <A, B, C> compose(f: Fun<B, C>): Fun<Fun<A, B>, Fun<A, C>> =
        { g -> { x -> f(g(x)) } }

    suspend fun <A, B, C> curry(f: Fun2<A, B, C>): Fun<A, Fun<B, C>> =
        { a -> { b -> f(a, b) } }

    suspend fun <A, B, C, D> curry(f: Fun3<A, B, C, D>): Fun<A, Fun<B, Fun<C, D>>> =
        { a -> { b -> { c -> f(a, b, c) } } }

    suspend fun <A, B, C> uncurry(f: Fun<A, Fun<B, C>>): Fun2<A, B, C> =
        { a, b -> f(a)(b) }

    object Infix {
        suspend infix fun <A, B, C> (Fun<B, C>).compose(g: Fun<A, B>): Fun<A, C> =
            { x -> this(g(x)) }

        suspend infix fun <A, B, C> (Fun<A, B>).then(g: Fun<B, C>): Fun<A, C> =
            g.compose(this)
    }

}
