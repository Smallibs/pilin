package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.standard.Validated.TK.Companion.fix
import io.smallibs.pilin.type.App

object Validated {

    sealed class T<L, R> : App<TK<L>, R> {
        data class Invalid<L, R>(val value: L) : T<L, R>()
        data class Valid<L, R>(val value: R) : T<L, R>()
    }

    class TK<L> private constructor() {
        companion object {
            val <L, R> App<TK<L>, R>.fix: T<L, R>
                get() = this as T<L, R>
        }
    }

    object Incarnation {
        class FunctorImpl<L>: Functor.API<TK<L>> {
            override suspend fun <A, B> map(ma: App<TK<L>, A>): suspend (suspend (A) -> B) -> App<TK<L>, B> = { f ->
                when(val a = ma.fix) {
                    is T.Valid -> T.Valid(f(a.value))
                    is T.Invalid -> T.Invalid(a.value)
                }
            }
        }

        class ApplicativeImpl<L>(private val functor: Functor.API<TK<L>> = FunctorImpl()) : Applicative.API<TK<L>>, Functor.API<TK<L>> by functor {
                override suspend fun <A> pure(a: A): App<TK<L>, A> = T.Valid(a)

                override suspend fun <A, B> apply(mf: App<TK<L>, suspend (A) -> B>): suspend (App<TK<L>, A>) -> App<TK<L>, B> =  { ma ->
                    when (val f = mf.fix) {
                        is T.Valid ->
                            when (val a = ma.fix) {
                                is T.Valid -> T.Valid(f.value(a.value))
                                is T.Invalid -> T.Invalid(a.value)
                            }
                        is T.Invalid ->T.Invalid(f.value)
                    }
                }

            override suspend fun <A, B> map(ma: App<TK<L>, A>): suspend (suspend (A) -> B) -> App<TK<L>, B> {
                return functor.map(ma)
            }
        }
    }
}