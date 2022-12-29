package io.smallibs.pilin.standard.`try`

import io.smallibs.pilin.abstractions.Semigroup
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

sealed interface Try<out A> : App<Try.TryK, A> {
    data class Success<out A>(val value: A) : Try<A>
    data class Failure(val error: Throwable) : Try<Nothing>

    // This code can be automatically generated
    object TryK {
        private val <A> App<TryK, A>.fix: Try<A>
            get() = this as Try<A>

        suspend fun <A, B> App<TryK, A>.fold(n: Fun<Throwable, B>, s: Fun<A, B>): B = when (val self = this.fix) {
            is Success -> s(self.value)
            is Failure -> n(self.error)
        }
    }

    companion object {
        fun <A> success(a: A): Try<A> = Success(a)
        fun <A> failure(e: Throwable): Try<A> = Failure(e)

        val functor = Functor.functor
        val applicative = Applicative.applicative
        val selective = Selective.selective
        val monad = Monad.monad
    }
}