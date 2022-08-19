package io.smallibs.pilin.abstractions

import io.smallibs.pilin.abstractions.PreCategory.FromMonad.FunK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.App2
import io.smallibs.pilin.type.Fun

object PreCategory {

    interface Core<T> {
        suspend fun <A, B, C> compose(f: App2<T, B, C>, g: App2<T, A, B>): App2<T, A, C>
    }

    interface WithCompose<T> : Core<T>

    open class FromMonad<F>(open val monad: Monad.Core<F>) : Core<FunK<F>> {
        private data class TMonad<F, A, B>(var f: Fun<A, App<F, B>>) : App2<FunK<F>, A, B>

        class FunK<F> private constructor() {
            companion object {
                fun <F, A, B> lift(f: Fun<A, App<F, B>>): App2<FunK<F>, A, B> = TMonad(f)
            }
        }

        private val <A, B> App2<FunK<F>, A, B>.fix: Fun<A, App<F, B>> get() = (this as TMonad<F, A, B>).f

        override suspend fun <A, B, C> compose(
            f: App2<FunK<F>, B, C>,
            g: App2<FunK<F>, A, B>,
        ): App2<FunK<F>, A, C> {
            return TMonad(monad.leftToRight<A, B, C>(g.fix)(f.fix))
        }
    }

    open class Operation<T>(private val c: Core<T>) : Core<T> by c {
        suspend fun <A, B, C> composeLeftToRight(
            g: App2<T, B, C>,
            f: App2<T, A, B>,
        ): App2<T, A, C> = c.compose(g, f)
    }

    open class Infix<T>(private val c: Core<T>) : Core<T> by c {
        suspend infix fun <A, B, C> App2<T, B, C>.composeRightToLeft(g: App2<T, A, B>): App2<T, A, C> =
            c.compose(this, g)

        suspend infix fun <A, B, C> App2<T, A, B>.composeLeftToRight(g: App2<T, B, C>): App2<T, A, C> =
            c.compose(g, this)

        suspend infix fun <A, B, C> App2<T, A, B>.andThen(g: App2<T, B, C>): App2<T, A, C> = c.compose(g, this)
    }

    interface API<T> : Core<T> {
        val operation: Operation<T> get() = Operation(this)
        val infix: Infix<T> get() = Infix(this)
    }

}