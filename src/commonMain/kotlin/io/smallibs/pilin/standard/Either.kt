package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.core.Fun.curry
import io.smallibs.pilin.module.open
import io.smallibs.pilin.standard.Either.TK.Companion.fix
import io.smallibs.pilin.type.App

object Either {

    sealed class T<L, R> : App<TK<L>, R> {
        data class Left<L, R>(val value: L) : T<L, R>()
        data class Right<L, R>(val value: R) : T<L, R>()
    }

    class TK<L> private constructor() {
        companion object {
            val <L, R> App<TK<L>, R>.fix: T<L, R>
                get() = this as T<L, R>
        }
    }

    private class FunctorImpl<L> : Functor.API<TK<L>> {
        override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<TK<L>, A>) -> App<TK<L>, B> =
            { ma ->
                when (val a = ma.fix) {
                    is T.Left -> T.Left(a.value)
                    is T.Right -> T.Right(f(a.value))
                }
            }
    }

    private class ApplicativeImpl<L>(override val functor: Functor.Core<TK<L>>) :
        Applicative.API<TK<L>>,
        Applicative.WithPureMapAndProduct<TK<L>>,
        Applicative.ViaFunctor<TK<L>> {
        override suspend fun <R> pure(a: R): App<TK<L>, R> = T.Right(a)

        override suspend fun <A, B> product(ma: App<TK<L>, A>): suspend (mb: App<TK<L>, B>) -> App<TK<L>, Pair<A, B>> =
            { mb ->
                when (val a = ma.fix) {
                    is T.Right ->
                        when (val b = mb.fix) {
                            is T.Right -> T.Right(a.value to b.value)
                            is T.Left -> T.Left(b.value)
                        }
                    is T.Left -> T.Left(a.value)
                }
            }
    }

    private class MonadImpl<L>(override val applicative: Applicative.API<TK<L>>) :
        Monad.API<TK<L>>,
        Monad.WithReturnsMapAndJoin<TK<L>>,
        Monad.ViaApplicative<TK<L>>,
        Applicative.Core<TK<L>> by applicative {
        override suspend fun <A> join(mma: App<TK<L>, App<TK<L>, A>>): App<TK<L>, A> =
            when (val ma = mma.fix) {
                is T.Right -> ma.value
                is T.Left -> T.Left(ma.value)
            }

        override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<TK<L>, A>) -> App<TK<L>, B> {
            return applicative.map(f)
        }
    }

    fun <L> functor(): Functor.API<TK<L>> = FunctorImpl()

    fun <L> applicative(): Applicative.API<TK<L>> = ApplicativeImpl(functor())

    fun <L> monad(): Monad.API<TK<L>> = MonadImpl(applicative())

}