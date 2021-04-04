package io.smallibs.pilin.control.fluent

import io.smallibs.pilin.control.Selective
import io.smallibs.pilin.standard.Either
import io.smallibs.pilin.type.App

open class FluentSelective<F>(private val selective: Selective.API<F>) : FluentFunctor<F>(selective) {
    suspend fun <A> pure(a: A): App<F, A> = selective.pure(a)
    suspend infix fun <A, B> Either.T<A, B>.select(f: App<F, suspend (A) -> B>): App<F, B> =
        selective.select(this)(f)
    suspend infix fun <A, B, C> Either.T<A, B>.branch(fs: Pair<App<F, suspend (A) -> C>, App<F, suspend (B) -> C>>): App<F, C> =
        selective.branch<A, B, C>(this)(fs.first)(fs.second)

    companion object {
        fun <F, R> Selective.API<F>.fluent(block: FluentSelective<F>.() -> R): R =
            FluentSelective(this).block()
    }
}