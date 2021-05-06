package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.Either.TK.Companion.fold
import io.smallibs.pilin.standard.Either.TK.Companion.left
import io.smallibs.pilin.standard.Either.TK.Companion.right
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Either {

    sealed class T<L, R> : App<TK<L>, R> {
        data class Left<L, R>(val value: L) : T<L, R>()
        data class Right<L, R>(val value: R) : T<L, R>()
    }

    // This code can be automatically generated
    class TK<L> private constructor() {
        companion object {
            private val <L, R> App<TK<L>, R>.fix: T<L, R> get() = this as T<L, R>

            fun <L, R> left(l: L): App<TK<L>, R> = T.Left(l)
            fun <L, R> right(r: R): App<TK<L>, R> = T.Right(r)

            suspend fun <L, R, B> App<TK<L>, R>.fold(l: Fun<L, B>, r: Fun<R, B>): B =
                when (val self = this.fix) {
                    is T.Left -> l(self.value)
                    is T.Right -> r(self.value)
                }
        }
    }

    private class FunctorImpl<L> : Functor.API<TK<L>> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<TK<L>, A>, App<TK<L>, B>> =
            { ma -> ma.fold(::left) { a -> right(f(a)) } }
    }

    private class ApplicativeImpl<L> :
        Applicative.API<TK<L>>,
        Applicative.WithPureAndApply<TK<L>> {
        override suspend fun <R> pure(a: R): App<TK<L>, R> = right(a)
        override suspend fun <A, B> apply(mf: App<TK<L>, Fun<A, B>>): Fun<App<TK<L>, A>, App<TK<L>, B>> =
            { ma -> mf.fold(::left) { f -> ma.fold(::left) { a -> pure(f(a)) } } }
    }

    private class MonadImpl<L>(applicative: Applicative.API<TK<L>>) :
        Monad.API<TK<L>>,
        Monad.WithReturnsMapAndJoin<TK<L>>,
        Monad.ViaApplicative<TK<L>>(applicative) {
        override suspend fun <A> join(mma: App<TK<L>, App<TK<L>, A>>): App<TK<L>, A> =
            mma.fold(::left) { it }
    }

    fun <L> functor(): Functor.API<TK<L>> = FunctorImpl()

    fun <L> applicative(): Applicative.API<TK<L>> = ApplicativeImpl()

    fun <L> monad(): Monad.API<TK<L>> = MonadImpl(applicative())

}