package io.smallibs.pilin.core

object Fun {

    fun <A> id(a: A): A = a

    suspend infix fun <A, B, C> (suspend (B) -> C).compose(g: suspend (A) -> B): suspend (A) -> C =
        { x -> this(g(x)) }

    suspend infix fun <A, B, C> (suspend (A) -> B).then(g: suspend (B) -> C): suspend (A) -> C =
        g.compose(this)

    suspend fun <A, B, C> curry(f: suspend (A, B) -> C): suspend (A) -> suspend (B) -> C =
        { a -> { b -> f(a, b) } }

    suspend fun <A, B, C> uncurry(f: suspend (A) -> suspend (B) -> C): suspend (A, B) -> C =
        { a, b -> f(a)(b) }

}
