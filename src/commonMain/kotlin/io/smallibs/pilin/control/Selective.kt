package io.smallibs.pilin.control

import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Selective {

    interface Core<F> {
        suspend fun <A> pure(a: A): App<F, A>
        suspend fun <A, B> select(e: Either<A, B>): Fun<App<F, Fun<A, B>>, App<F, B>>
        suspend fun <A, B, C> branch(e: Either<A, B>): Fun<App<F, Fun<A, C>>, Fun<App<F, Fun<B, C>>, App<F, C>>>
    }

    interface API<F> : Core<F>

}