package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.Option.TK.Companion.fix
import io.smallibs.pilin.type.App

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
            override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<TK, A>) -> App<TK, B> =
                { ma ->
                    when (val a = ma.fix) {
                        is T.Some -> T.Some(f(a.value))
                        is T.None -> T.None()
                    }
                }
        }

        class ApplicativeImpl :
            Applicative.WithPureAndApply<TK>,
            Applicative.API<TK> {
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
        }

        class MonadImpl(override val applicative: Applicative.API<TK>) : Monad.API<TK> {
            override suspend fun <A> join(mma: App<TK, App<TK, A>>): App<TK, A> =
                when (val ma = mma.fix) {
                    is T.Some -> ma.value
                    is T.None -> T.None()
                }
        }
    }

    val functor: Functor.API<TK> = Incarnation.FunctorImpl()

    val applicative: Applicative.API<TK> = Incarnation.ApplicativeImpl()

    val monad: Monad.API<TK> = Incarnation.MonadImpl(applicative)

}