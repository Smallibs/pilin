package io.smallibs.kategory.control

import io.smallibs.kategory.type.App
import io.smallibs.kategory.type.Fun

interface Functor<F> {
    suspend fun <A, B> map(ma: App<F, A>): suspend (Fun.T<A, B>) -> App<F, B>
}

