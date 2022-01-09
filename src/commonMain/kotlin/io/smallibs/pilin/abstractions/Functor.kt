package io.smallibs.pilin.abstractions

import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {

    interface Core<F> {
        suspend fun <A, B> map(f: Fun<A, B>): Fun<App<F, A>, App<F, B>>
    }

    class Operation<F>(private val c: Core<F>) : Core<F> by c {
        suspend fun <A, B> replace(a: A): Fun<App<F, B>, App<F, A>> =
            map { a }

        suspend fun <A> void(ma: App<F, A>): App<F, Unit> =
            replace<Unit, A>(Unit)(ma)
    }

    open class Infix<F>(private val c: Core<F>) : Core<F> by c {
        suspend infix fun <A, B> Fun<A, B>.map(ma: App<F, A>): App<F, B> = c.map(this)(ma)
        suspend infix fun <A, B> App<F, A>.map(f: Fun<A, B>): App<F, B> = c.map(f)(this)
    }

    interface API<F> : Core<F> {
        val operation: Operation<F> get() = Operation(this)
        val infix: Infix<F> get() = Infix(this)
    }

}
