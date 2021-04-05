package io.smallibs.pilin.control

import io.smallibs.pilin.standard.Either
import io.smallibs.pilin.type.App

object Selective {
    interface WithSelect<F> {
        suspend fun <A, B> select(e: Either.T<A, B>):
                suspend (App<F, suspend (A) -> B>) -> App<F, B>
    }

    interface WithBranch<F> {
        suspend fun <A, B, C> branch(e: Either.T<A, B>):
                suspend (App<F, suspend (A) -> C>) -> suspend (App<F, suspend (B) -> C>) -> App<F, C>
    }

    interface PureWithSelect<F> : WithSelect<F> {
        suspend fun <A> pure(a: A): App<F, A>
    }

    interface PureWithBranch<F> : WithBranch<F> {
        suspend fun <A> pure(a: A): App<F, A>
    }

    interface API<F> : WithSelect<F>, WithBranch<F>, Applicative.API<F>

}