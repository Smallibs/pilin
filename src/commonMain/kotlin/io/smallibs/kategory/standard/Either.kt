package io.smallibs.kategory.standard

import io.smallibs.kategory.control.Applicative
import io.smallibs.kategory.control.Functor
import io.smallibs.kategory.control.Monad
import io.smallibs.kategory.standard.Either.TK.Companion.fix
import io.smallibs.kategory.type.App

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

    object Incarnation {
        class FunctorImpl<L> : Functor.API<TK<L>> {
            override suspend fun <A, B> map(ma: App<TK<L>, A>): suspend (suspend (A) -> B) -> App<TK<L>, B> =
                { f ->
                    when (val a = ma.fix) {
                        is T.Left -> T.Left(a.value)
                        is T.Right -> T.Right(f(a.value))
                    }
                }
        }

        class ApplicativeImpl<L>(val functor: Functor.API<TK<L>>) : Applicative.API<TK<L>>,
            Functor.API<TK<L>> by functor {
            override suspend fun <R> pure(a: R): App<TK<L>, R> = T.Right(a)

            override suspend fun <A, B> apply(mf: App<TK<L>, suspend (A) -> B>): suspend (App<TK<L>, A>) -> App<TK<L>, B> =
                { ma ->
                    when (val f = mf.fix) {
                        is T.Right ->
                            when (val a = ma.fix) {
                                is T.Right -> T.Right(f.value(a.value))
                                is T.Left -> T.Left(a.value)
                            }
                        is T.Left -> T.Left(f.value)
                    }
                }

            override suspend fun <A, B> map(ma: App<TK<L>, A>): suspend (suspend (A) -> B) -> App<TK<L>, B> {
                return functor.map(ma)
            }
        }

        class MonadImpl<L>(val applicative: Applicative.API<TK<L>>) : Monad.API<TK<L>>,
            Applicative.API<TK<L>> by applicative {
            override suspend fun <A> join(mma: App<TK<L>, App<TK<L>, A>>): App<TK<L>, A> =
                when (val ma = mma.fix) {
                    is T.Right -> ma.value
                    is T.Left -> T.Left(ma.value)
                }
        }
    }

    fun <L> Functor(): Functor.API<TK<L>> = Incarnation.FunctorImpl()

    fun <L> Applicative(): Applicative.API<TK<L>> = Incarnation.ApplicativeImpl(Functor())

    fun <L> Monad(): Monad.API<TK<L>> = Incarnation.MonadImpl(Applicative())

}