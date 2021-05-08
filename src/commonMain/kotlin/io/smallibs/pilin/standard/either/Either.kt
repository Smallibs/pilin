package io.smallibs.pilin.standard.either

import io.smallibs.pilin.standard.either.Either.TK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

sealed class Either<L, R> : App<TK<L>, R> {
    data class Left<L, R>(val value: L) : Either<L, R>()
    data class Right<L, R>(val value: R) : Either<L, R>()

    // This code can be automatically generated
    class TK<L> private constructor() {
        companion object {
            private val <L, R> App<TK<L>, R>.fix: Either<L, R> get() = this as Either<L, R>

            suspend fun <L, R, B> App<TK<L>, R>.fold(l: Fun<L, B>, r: Fun<R, B>): B =
                when (val self = this.fix) {
                    is Left -> l(self.value)
                    is Right -> r(self.value)
                }
        }
    }

    companion object {
        fun <L, R> left(l: L): App<TK<L>, R> = Left(l)
        fun <L, R> right(r: R): App<TK<L>, R> = Right(r)

        fun <L> functor() = Functor.functor<L>()
        fun <L> applicative() = Applicative.applicative<L>()
        fun <L> monad() = Monad.monad<L>()
    }
}
