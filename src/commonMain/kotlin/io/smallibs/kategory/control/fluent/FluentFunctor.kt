package io.smallibs.kategory.control.fluent

import io.smallibs.kategory.control.Functor
import io.smallibs.kategory.type.App

open class FluentFunctor<F>(private val functor: Functor.API<F>) {
    suspend infix fun <A, B> App<F, A>.map(f: suspend (A) -> B): App<F, B> = functor.map<A, B>(this)(f)

    companion object {
        fun <F, R> Functor.API<F>.fluent(block: FluentFunctor<F>.() -> R): R =
            FluentFunctor(this).block()
    }
}