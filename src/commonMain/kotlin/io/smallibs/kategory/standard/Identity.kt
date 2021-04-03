package io.smallibs.kategory.standard

import io.smallibs.kategory.control.Applicative
import io.smallibs.kategory.control.Functor
import io.smallibs.kategory.control.Monad
import io.smallibs.kategory.standard.Identity.TK.Companion.fix
import io.smallibs.kategory.type.App
import io.smallibs.kategory.type.Fun

object Identity {

    data class T<A>(val v: A) : App<TK, A>

    class TK private constructor() {
        companion object {
            fun <A> App<TK, A>.fix(): T<A> =
                this as T<A>
        }
    }

    object Incarnation {
        class FunctorImpl : Functor<TK> {
            override suspend fun <A, B> map(ma: App<TK, A>): suspend (Fun.T<A, B>) -> App<TK, B> = { f ->
                T(f(ma.fix().v))
            }
        }

        class ApplicativeImpl(override val functor: Functor<TK>) : Applicative<TK>, Functor<TK> by functor {
            override suspend fun <A> pure(a: A): App<TK, A> =
                T(a)

            override suspend fun <A, B> apply(mf: App<TK, Fun.T<A, B>>): suspend (App<TK, A>) -> App<TK, B> =
                { ma -> T(mf.fix().v(ma.fix().v)) }

            override suspend fun <A, B> map(ma: App<TK, A>): suspend (Fun.T<A, B>) -> App<TK, B> =
                functor.map(ma)
        }

        class MonadImpl(override val applicative: Applicative<TK>) : Monad<TK>, Applicative<TK> by applicative {
            override suspend fun <A> join(mma: App<TK, App<TK, A>>): App<TK, A> =
                mma.fix().v
        }
    }

    val Functor: Functor<TK> = Incarnation.FunctorImpl()

    val Applicative: Applicative<TK> = Incarnation.ApplicativeImpl(Functor)

    val Monad: Monad<TK> = Incarnation.MonadImpl(Applicative)

}