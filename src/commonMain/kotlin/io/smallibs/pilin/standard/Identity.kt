package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.Identity.TK.Companion.fix
import io.smallibs.pilin.type.App

object Identity {

    data class T<A>(val v: A) : App<TK, A>

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
                    T(f(ma.fix.v))
                }
        }

        class ApplicativeImpl :
            Applicative.WithPureAndApply<TK>,
            Applicative.API<TK> {
            override suspend fun <A> pure(a: A): App<TK, A> =
                T(a)

            override suspend fun <A, B> apply(mf: App<TK, suspend (A) -> B>): suspend (App<TK, A>) -> App<TK, B> =
                { ma -> T(mf.fix.v(ma.fix.v)) }
        }

        class MonadImpl(override val applicative: Applicative.API<TK>) : Monad.API<TK> {
            override suspend fun <A> join(mma: App<TK, App<TK, A>>): App<TK, A> =
                mma.fix.v
        }
    }

    val functor: Functor.API<TK> = Incarnation.FunctorImpl()

    val applicative: Applicative.API<TK> = Incarnation.ApplicativeImpl()

    val monad: Monad.API<TK> = Incarnation.MonadImpl(applicative)

}