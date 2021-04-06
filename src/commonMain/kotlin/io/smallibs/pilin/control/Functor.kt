package io.smallibs.pilin.control

import io.smallibs.pilin.type.App

object Functor {

    interface Core<F> {
        suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<F, A>) -> App<F, B>
    }

    class Operation<F>(private val c: Core<F>) : Core<F> by c {
        suspend fun <A, B> replace(a: A): suspend (App<F, B>) -> App<F, A> =
            map { a }

        suspend fun <A> void(ma: App<F, A>): App<F, Unit> =
            replace<Unit, A>(Unit)(ma)
    }

    open class Infix<F>(private val c: Core<F>) : Core<F> by c {
        suspend infix fun <A, B> (suspend (A) -> B).map(ma: App<F, A>): App<F, B> = c.map(this)(ma)
    }

    interface API<F> : Core<F> {
        val operation: Operation<F> get() = Operation(this)
        val infix: Infix<F> get() = Infix(this)
    }

}
