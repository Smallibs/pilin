package io.smallibs.kategory.control

import io.smallibs.kategory.type.App
import io.smallibs.kategory.type.Fun

interface Monad<F> {
    val applicative: Applicative<F>

    suspend fun <A> returns(a: A): App<F, A> = applicative.pure(a)
    suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A>
    suspend fun <A, B> bind(ma: App<F, A>): suspend (Fun.T<A, App<F, B>>) -> App<F, B> = { f ->
        join(applicative.map<A, App<F, B>>(ma)(f))
    }
}

