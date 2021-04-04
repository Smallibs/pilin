package io.smallibs.pilin.control

import io.smallibs.pilin.type.App

object Applicative {
    interface API<F> : Functor.API<F> {
        suspend fun <A> pure(a: A): App<F, A>
        suspend fun <A, B> apply(mf: App<F, suspend (A) -> B>): suspend (App<F, A>) -> App<F, B>
        override suspend fun <A, B> map(ma: App<F, A>): suspend (suspend (A) -> B) -> App<F, B> =
            { f -> apply(pure(f))(ma) }
    }
}