package io.smallibs.pilin.abstractions.comprehension

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.comprehension.continuation.Reflection
import io.smallibs.pilin.type.App

class DefaultComprehension<F>(private val monad: Monad.API<F>) : Monad.API<F> by monad, Comprehension<F> {

    var reflection = Reflection.represents(monad)

    override suspend fun <B> App<F, B>.bind(): B = reflection.reflect(this)

    companion object {
        suspend inline fun <F, A> run(
            monad: Monad.API<F>,
            crossinline f: suspend DefaultComprehension<F>.() -> A,
        ): App<F, A> =
            DefaultComprehension(monad).let { comprehension ->
                comprehension.reflection.reify { comprehension.f() }
            }
    }
}