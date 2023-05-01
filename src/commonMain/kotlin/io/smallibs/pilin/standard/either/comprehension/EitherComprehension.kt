package io.smallibs.pilin.standard.either.comprehension

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.comprehension.Comprehension
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.either.Either.EitherK.Companion.fold
import io.smallibs.pilin.type.App

class EitherComprehension<L>(private val monad: Monad.API<Either.EitherK<L>>) :
    Monad.API<Either.EitherK<L>> by monad, Comprehension<Either.EitherK<L>> {

    class CarriedResult(val value: Any) : Exception()

    override suspend fun <B> App<Either.EitherK<L>, B>.bind(): B =
        this.fold({ throw CarriedResult(it as Any) }, { it })

    companion object {
        suspend inline fun <L, R> run(
            monad: Monad.API<Either.EitherK<L>>,
            crossinline f: suspend EitherComprehension<L>.() -> R,
        ): App<Either.EitherK<L>, R> =
            EitherComprehension(monad).let { comprehension ->
                try {
                    monad.returns(comprehension.f())
                } catch (e: CarriedResult) {
                    @Suppress("UNCHECKED_CAST")
                    Either.left(e.value as L)
                }
            }
    }
}