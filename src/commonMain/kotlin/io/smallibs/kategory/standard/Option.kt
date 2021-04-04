package io.smallibs.kategory.standard

import io.smallibs.kategory.control.Applicative
import io.smallibs.kategory.control.Functor
import io.smallibs.kategory.control.Monad
import io.smallibs.kategory.standard.Option.TK.Companion.fix
import io.smallibs.kategory.type.App

object Option {

    sealed class T<A> : App<TK, A> {
        class None<A> : T<A>()
        data class Some<A>(val value: A) : T<A>()
    }

    class TK private constructor() {
        companion object {
            val <A> App<TK, A>.fix: T<A>
                get() = this as T<A>
        }
    }

    object Incarnation {
        class FunctorImpl : Functor.API<TK> {
            override suspend fun <A, B> map(ma: App<TK, A>): suspend (suspend (A) -> B) -> App<TK, B> =
                { f ->
                    when (val a = ma.fix) {
                        is T.Some -> T.Some(f(a.value))
                        is T.None -> T.None()
                    }
                }
        }

        class ApplicativeImpl(val functor: Functor.API<TK>) : Applicative.API<TK>, Functor.API<TK> by functor {
            override suspend fun <A> pure(a: A): App<TK, A> =
                T.Some(a)

            override suspend fun <A, B> apply(mf: App<TK, suspend (A) -> B>): suspend (App<TK, A>) -> App<TK, B> =
                { ma ->
                    when (val f = mf.fix) {
                        is T.Some ->
                            when (val a = ma.fix) {
                                is T.Some -> T.Some(f.value(a.value))
                                is T.None -> T.None()
                            }
                        is T.None -> T.None()
                    }
                }

            override suspend fun <A, B> map(ma: App<TK, A>): suspend (suspend (A) -> B) -> App<TK, B> =
                functor.map(ma)
        }

        class MonadImpl(val applicative: Applicative.API<TK>) : Monad.API<TK>,
            Applicative.API<TK> by applicative {
            override suspend fun <A> join(mma: App<TK, App<TK, A>>): App<TK, A> =
                when (val ma = mma.fix) {
                    is T.Some -> ma.value
                    is T.None -> T.None()
                }
        }
    }

    val Functor: Functor.API<TK> = Incarnation.FunctorImpl()

    val Applicative: Applicative.API<TK> = Incarnation.ApplicativeImpl(Functor)

    val Monad: Monad.API<TK> = Incarnation.MonadImpl(Applicative)

}