package io.smallibs.pilin.abstractions.comprehension

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.comprehension.continuation.Reflection
import io.smallibs.pilin.type.App

class Comprehension<F>(private val monad: Monad.API<F>) : Monad.API<F> by monad {

    var reflection = Reflection.represents(monad)

    suspend inline fun <B> App<F, B>.bind(): B = reflection.reflect(this)

    companion object {
        suspend inline fun <F, A> run(monad: Monad.API<F>, crossinline f: suspend Comprehension<F>.() -> A): App<F, A> =
            Comprehension(monad).let { comprehension ->
                comprehension.reflection.reify { comprehension.f() }
            }
    }
}