package io.smallibs.pilin.standard.option.comprehension

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.comprehension.Comprehension
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.standard.option.Option.OptionK.fold
import io.smallibs.pilin.type.App

class OptionComprehension(private val monad: Monad.API<Option.OptionK>) :
    Monad.API<Option.OptionK> by monad, Comprehension<Option.OptionK> {

    class OptionException : Exception()

    override suspend fun <B> App<Option.OptionK, B>.bind(): B =
        this.fold({ throw OptionException() }, { it })

    companion object {
        suspend inline fun <A> run(
            monad: Monad.API<Option.OptionK>,
            crossinline f: suspend OptionComprehension.() -> A,
        ): App<Option.OptionK, A> =
            OptionComprehension(monad).let { comprehension ->
                try {
                    monad.returns(comprehension.f())
                } catch (e: OptionException) {
                    Option.none()
                }
            }
    }
}