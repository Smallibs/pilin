package io.smallibs.pilin.standard.result.comprehension

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.comprehension.Comprehension
import io.smallibs.pilin.standard.result.Result
import io.smallibs.pilin.standard.result.Result.ResultK.Companion.fold
import io.smallibs.pilin.type.App

class ResultComprehension<E>(private val monad: Monad.API<Result.ResultK<E>>) :
    Monad.API<Result.ResultK<E>> by monad, Comprehension<Result.ResultK<E>> {

    class CarriedResult(val value: Any) : Exception()

    override suspend fun <B> App<Result.ResultK<E>, B>.bind(): B =
        this.fold({ throw CarriedResult(it as Any) }, { it })

    companion object {
        suspend inline fun <E, A> run(
            monad: Monad.API<Result.ResultK<E>>,
            crossinline f: suspend ResultComprehension<E>.() -> A,
        ): App<Result.ResultK<E>, A> =
            ResultComprehension(monad).let { comprehension ->
                try {
                    monad.returns(comprehension.f())
                } catch (e: CarriedResult) {
                    Result.error(e.value as E)
                }
            }
    }
}