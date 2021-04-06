package io.smallibs.pilin.control

import io.smallibs.pilin.standard.Either
import io.smallibs.pilin.type.App

object Selective {

    interface Core<F> {
        suspend fun <A> pure(a: A): App<F, A>
        suspend fun <A, B> select(e: Either.T<A, B>): suspend (App<F, suspend (A) -> B>) -> App<F, B>
        suspend fun <A, B, C> branch(e: Either.T<A, B>): suspend (App<F, suspend (A) -> C>) -> suspend (App<F, suspend (B) -> C>) -> App<F, C>
    }

    interface API<F> : Core<F>

}