package io.smallibs.pilin.extension

import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.delimited.Reflection
import io.smallibs.pilin.type.App

class Comprehension<F, A>(private val monad: Monad.Core<F>) : Monad.Core<F> by monad {

    private var reflection = Reflection.represents(monad)

    suspend fun <B> App<F, B>.bind(): B =
        reflection.reflect(this)

    companion object {
        suspend infix fun <F, A> Monad.Core<F>.`do`(f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            invoke(this, f)

        suspend operator fun <F, A> invoke(monad: Monad.Core<F>, f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            Comprehension<F, A>(monad).let { comprehension ->
                comprehension.reflection.reify { comprehension.f() }
            }
    }
}