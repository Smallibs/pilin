package io.smallibs.pilin.control.fluent

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.type.App

open class FluentApplicative<F>(private val applicative: Applicative.API<F>) : FluentFunctor<F>(applicative) {
    suspend fun <A> pure(a: A): App<F, A> = applicative.pure(a)
    suspend infix fun <A, B> App<F, suspend (A) -> B>.apply(ma: App<F, A>): App<F, B> = applicative.apply(this)(ma)

    companion object {
        fun <F, R> Applicative.API<F>.fluent(block: FluentApplicative<F>.() -> R): R =
            FluentApplicative(this).block()
    }
}