package io.smallibs.kategory.control

import io.smallibs.kategory.type.App
import io.smallibs.kategory.type.Fun

interface Applicative<F> {
    val functor: Functor<F>

    suspend fun <A> pure(a: A): App<F, A>

    suspend fun <A, B> apply(mf: App<F, Fun.T<A, B>>): suspend (App<F, A>) -> App<F, B>

    suspend fun <A, B> map(ma: App<F, A>): suspend (Fun.T<A, B>) -> App<F, B> = { f -> apply(pure(f))(ma) }
}
