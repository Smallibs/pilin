package io.smallibs.pilin.core

typealias Lambda<A, B> = suspend (A) -> B
typealias Compose<A, B, C> = Lambda<Lambda<B, C>, Lambda<Lambda<A, B>, Lambda<A, C>>>

object Fun {

    suspend fun <A> id(a: A): A = a

    suspend fun <A,B,C> flip(f: suspend (A) -> suspend (B) -> C) : suspend (B) -> suspend (A) -> C =
        { b -> { a -> f(a)(b) }}

    suspend infix fun <A, B, C> compose(f: suspend (B) -> C): suspend (suspend (A) -> B) -> suspend (A) -> C =
        { g -> { x -> f(g(x)) } }

    suspend fun <A, B, C> curry(f: suspend (A, B) -> C): suspend (A) -> suspend (B) -> C =
        { a -> { b -> f(a, b) } }

    suspend fun <A, B, C> uncurry(f: suspend (A) -> suspend (B) -> C): suspend (A, B) -> C =
        { a, b -> f(a)(b) }

    object Infix {
        suspend infix fun <A, B, C> (suspend (B) -> C).compose(g: suspend (A) -> B): suspend (A) -> C =
            { x -> this(g(x)) }

        suspend infix fun <A, B, C> (suspend (A) -> B).then(g: suspend (B) -> C): suspend (A) -> C =
            g.compose(this)
    }

}
