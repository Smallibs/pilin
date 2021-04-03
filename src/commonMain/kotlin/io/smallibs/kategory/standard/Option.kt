package io.smallibs.kategory.standard

import io.smallibs.kategory.control.Applicative
import io.smallibs.kategory.control.Functor
import io.smallibs.kategory.control.Monad
import io.smallibs.kategory.standard.Option.TK.Companion.fix
import io.smallibs.kategory.type.App
import io.smallibs.kategory.type.Fun

object Option {

    sealed class T<A> : App<TK, A> {
        class None<A> : T<A>()
        data class Some<A>(val value: A) : T<A>()
    }

    class TK private constructor() {
        companion object {
            fun <A> App<TK, A>.fix(): T<A> =
                this as T<A>
        }
    }

    object Incarnation {
        class FunctorImpl : Functor<TK> {
            override suspend fun <A, B> map(ma: App<TK, A>): suspend (Fun.T<A, B>) -> App<TK, B> =
                { f ->
                    when (val a = ma.fix()) {
                        is T.Some -> T.Some(f(a.value))
                        is T.None -> T.None()
                    }
                }
        }

        class ApplicativeImpl(override val functor: Functor<TK>) : Applicative<TK>, Functor<TK> by functor {
            override suspend fun <A> pure(a: A): App<TK, A> =
                T.Some(a)

            override suspend fun <A, B> apply(mf: App<TK, Fun.T<A, B>>): suspend (App<TK, A>) -> App<TK, B> =
                { ma ->
                    when (val f = mf.fix()) {
                        is T.Some ->
                            when (val a = ma.fix()) {
                                is T.Some<A> -> T.Some(f.value(a.value))
                                is T.None<A> -> T.None()
                            }
                        is T.None -> T.None()
                    }
                }

            override suspend fun <A, B> map(ma: App<TK, A>): suspend (Fun.T<A, B>) -> App<TK, B> =
                functor.map(ma)
        }

        class MonadImpl(override val applicative: Applicative<TK>) : Monad<TK>, Applicative<TK> by applicative {
            override suspend fun <A> join(mma: App<TK, App<TK, A>>): App<TK, A> =
                when (val ma = mma.fix()) {
                    is T.Some -> ma.value
                    is T.None -> T.None()
                }
        }
    }

    val Functor: Functor<TK> = Incarnation.FunctorImpl()

    val Applicative: Applicative<TK> = Incarnation.ApplicativeImpl(Functor)

    val Monad: Monad<TK> = Incarnation.MonadImpl(Applicative)

}