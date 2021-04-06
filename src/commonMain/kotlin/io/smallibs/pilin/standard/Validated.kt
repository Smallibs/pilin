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

    private class FunctorImpl<L> : Functor.API<TK<L>> {
        override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<TK<L>, A>) -> App<TK<L>, B> =
            { ma ->
                when (val a = ma.fix) {
                    is T.Invalid -> T.Invalid(a.value)
                    is T.Valid -> T.Valid(f(a.value))
                }
            }
    }

    private class ApplicativeImpl<L> :
        Applicative.API<TK<L>>,
        Applicative.WithPureAndApply<TK<L>> {
        override suspend fun <R> pure(a: R): App<TK<L>, R> = T.Valid(a)

        override suspend fun <A, B> apply(mf: App<TK<L>, suspend (A) -> B>): suspend (App<TK<L>, A>) -> App<TK<L>, B> = { ma ->
            when(val a = ma.fix) {
                is T.Valid -> when(val f = mf.fix) {
                    is T.Valid -> pure(f.value(a.value))
                    is T.Invalid -> T.Invalid(f.value)
                }
                is T.Invalid -> T.Invalid(a.value)
            }
        }
    }

    fun <L> functor(): Functor.API<TK<L>> = FunctorImpl()

    fun <L> applicative(): Applicative.API<TK<L>> = ApplicativeImpl()
}