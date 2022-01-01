package io.smallibs.pilin.extension

import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.either.Either.EitherK.Companion.fold
import io.smallibs.pilin.type.App

class Comprehension<F, A>(private val monad: Monad.Core<F>) : Monad.Core<F> by monad {

    class ComprehensionException(val value: Any) : Exception()

    suspend fun <B> App<F, B>.bind(): B {
        var intermediate = left<App<F, B>, B>(this)

        monad.map<B, B> {
            intermediate = right(it)
            it
        }(this)

        return intermediate.fold({ throw ComprehensionException(this) }, { it });
    }

    companion object {
        suspend infix fun <F, A> Monad.Core<F>.`do`(f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            invoke(this, f)

        suspend operator fun <F, A> invoke(monad: Monad.Core<F>, f: suspend Comprehension<F, A>.() -> A): App<F, A> =
            Comprehension<F, A>(monad).let {
                @Suppress("UNCHECKED_CAST")
                try {
                    monad.pure(it.f())
                } catch (e: ComprehensionException) {
                    e.value
                } as App<F, A>
            }
    }
}