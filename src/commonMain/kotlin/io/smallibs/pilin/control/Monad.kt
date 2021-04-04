package io.smallibs.pilin.control

import io.smallibs.pilin.type.App

object Monad {
    interface API<F> : Applicative.API<F> {
        suspend fun <A> returns(a: A): App<F, A> = pure(a)
        suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A>
        suspend fun <A, B> bind(ma: App<F, A>): suspend (suspend (A) -> App<F, B>) -> App<F, B> = { f ->
            join(map<A, App<F, B>>(ma)(f))
        }
    }
}

