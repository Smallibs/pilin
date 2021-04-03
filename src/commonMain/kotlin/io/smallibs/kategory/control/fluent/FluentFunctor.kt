package io.smallibs.kategory.control.fluent

import io.smallibs.kategory.control.Functor
import io.smallibs.kategory.type.App
import io.smallibs.kategory.type.Fun

open class FluentFunctor<F>(private val functor: Functor<F>) {
    suspend infix fun <A, B> App<F, A>.map(f: Fun.T<A, B>): App<F, B> =
        functor.map<A, B>(this)(f)

    companion object {
        fun <F, R> Functor<F>.fluent(block: FluentFunctor<F>.() -> R): R =
            FluentFunctor(this).block()
    }
}