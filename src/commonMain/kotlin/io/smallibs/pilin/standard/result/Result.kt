package io.smallibs.pilin.standard.result

import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

sealed interface Result<out A, out E> : App<Result.ResultK<E>, A> {
    data class Success<out A, out E>(val value: A) : Result<A, E>
    data class Failure<out A, out E>(val value: E) : Result<A, E>

    // This code can be automatically generated
    class ResultK<out E> private constructor() {
        companion object {
            private val <A, E> App<ResultK<E>, A>.fix: Result<A, E>
                get() = this as Result<A, E>

            suspend fun <A, B, E> App<ResultK<E>, A>.fold(f: Fun<E, B>, s: Fun<A, B>): B =
                when (val self = this.fix) {
                    is Success -> s(self.value)
                    is Failure -> f(self.value)
                }
        }
    }

    companion object {
        fun <A, E> success(a: A): Result<A, E> = Success(a)
        fun <A, E> failure(e: E): Result<A, E> = Failure(e)

        fun <E> functor() = Functor.functor<E>()
        fun <E> applicative() = Applicative.applicative<E>()
        fun <E> selective() = Selective.selective<E>()
        fun <E> monad() = Monad.monad<E>()
    }
}