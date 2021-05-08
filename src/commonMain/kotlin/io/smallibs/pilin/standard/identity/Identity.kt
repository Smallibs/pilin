package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.standard.identity.Identity.TK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

data class Identity<A>(val value: A) : App<TK, A> {

    // This code can be automatically generated
    class TK private constructor() {
        companion object {
            private val <A> App<TK, A>.fix: Identity<A> get() = this as Identity<A>

            suspend fun <A, B> App<TK, A>.fold(f: Fun<A, B>): B = f(this.fix.value)
        }
    }

    companion object {
        fun <A> id(a: A): App<TK, A> = Identity(a)
    }
}
