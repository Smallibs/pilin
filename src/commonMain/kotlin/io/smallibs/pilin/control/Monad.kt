package io.smallibs.pilin.control

import io.smallibs.pilin.type.App

object Monad {

    interface WithJoin<F> {
        suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A>
    }

    interface API<F> : WithJoin<F>, Applicative.API<F> {
        val applicative: Applicative.API<F>

        suspend fun <A> returns(a: A): App<F, A> = pure(a)

        suspend fun <A, B> bind(ma: App<F, A>): suspend (suspend (A) -> App<F, B>) -> App<F, B> =
            { f ->
                join(map<A, App<F, B>>(ma)(f))
            }
    }

}

