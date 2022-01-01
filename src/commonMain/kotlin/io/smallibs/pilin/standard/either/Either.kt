package io.smallibs.pilin.standard.either

import io.smallibs.pilin.core.Standard.curry
import io.smallibs.pilin.standard.either.Either.EitherK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

sealed class Either<out L, out R> : App<EitherK<L>, R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()

    // This code can be automatically generated
    class EitherK<out L> private constructor() {
        companion object {
            private val <L, R> App<EitherK<L>, R>.fix: Either<L, R> get() = this as Either<L, R>

            suspend fun <L, R, B> App<EitherK<L>, R>.fold(l: Fun<L, B>, r: Fun<R, B>): B =
                when (val self = this.fix) {
                    is Left -> l(self.value)
                    is Right -> r(self.value)
                }

            suspend fun <L, R, A> fold(l: Fun<L, A>): Fun<Fun<R, A>, Fun<App<EitherK<L>, R>, A>> =
                curry { r, e -> e.fold(l, r) }
        }
    }

    companion object {
        fun <L, R> left(l: L): Either<L, R> = Left(l)
        fun <L, R> right(r: R): Either<L, R> = Right(r)

        fun <L> functor() = Functor.functor<L>()
        fun <L> applicative() = Applicative.applicative<L>()
        fun <O> selective() = Selective.selective<O>()
        fun <L> monad() = Monad.monad<L>()
    }
}
