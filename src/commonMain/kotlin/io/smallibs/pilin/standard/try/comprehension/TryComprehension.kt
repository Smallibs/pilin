package io.smallibs.pilin.standard.`try`.comprehension

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.comprehension.Comprehension
import io.smallibs.pilin.standard.`try`.Try
import io.smallibs.pilin.standard.`try`.Try.TryK.fold
import io.smallibs.pilin.type.App

class TryComprehension(private val monad: Monad.API<Try.TryK>) :
    Monad.API<Try.TryK> by monad, Comprehension<Try.TryK> {

    class CarriedException(val error: Throwable) : Exception()

    override suspend fun <B> App<Try.TryK, B>.bind(): B =
        this.fold({ throw CarriedException(it) }, { it })

    companion object {
        suspend inline fun <A> run(
            monad: Monad.API<Try.TryK>,
            crossinline f: suspend TryComprehension.() -> A,
        ): App<Try.TryK, A> =
            TryComprehension(monad).let { comprehension ->
                try {
                    monad.returns(comprehension.f())
                } catch (e: CarriedException) {
                    Try.failure(e.error)
                }
            }
    }
}