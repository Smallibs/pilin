package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.standard.identity.Identity.IdentityK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

data class Identity<A>(val value: A) : App<IdentityK, A> {

    // This code can be automatically generated
    class IdentityK private constructor() {
        companion object {
            private val <A> App<IdentityK, A>.fix: Identity<A> get() = this as Identity<A>

            suspend fun <A, B> App<IdentityK, A>.fold(f: Fun<A, B>): B = f(this.fix.value)
        }
    }

    companion object {
        fun <A> id(a: A): Identity<A> = Identity(a)

        val functor = Functor.functor
        val applicative = Applicative.applicative
        val selective = Selective.selective
        val monad = Monad.monad
    }
}
