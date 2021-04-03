package io.smallibs.kategory.control.fluent

import io.smallibs.kategory.control.Monad
import io.smallibs.kategory.type.App
import io.smallibs.kategory.type.Fun

open class FluentMonad<F>(private val monad: Monad<F>) : FluentApplicative<F>(monad.applicative) {
    suspend fun <A> App<F, App<F, A>>.join(): App<F, A> = monad.join(this)
    suspend infix fun <A, B> App<F, A>.bind(f: Fun.T<A, App<F, B>>): App<F, B> = monad.bind<A, B>(this)(f)
    suspend fun <A> returns(a: A): App<F, A> = monad.returns(a)

    companion object {
        fun <F, R> Monad<F>.fluent(block: FluentMonad<F>.() -> R): R =
            FluentMonad(this).block()
    }
}
