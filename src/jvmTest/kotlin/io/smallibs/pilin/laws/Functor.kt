package io.smallibs.pilin.laws

import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.core.Fun.Infix.compose
import io.smallibs.pilin.core.Fun.id
import io.smallibs.pilin.module.open
import io.smallibs.pilin.type.App

object Functor {

    suspend fun <F, A> `map id = id`(
        functor: Functor.API<F>,
        x: App<F, A>
    ) {
        open(functor) {
            assert(map<A, A> { i -> id(i) }(x) == id(x))
        }
    }

    suspend fun <F, A, B, C> `map (f compose g) = map f compose map g`(
        functor: Functor.API<F>,
        f: suspend (B) -> C,
        g: suspend (A) -> B,
        x: App<F, A>
    ) {
        open(functor) {
            assert(map(f compose g)(x) == (map(f) compose map(g))(x))
        }
    }

}