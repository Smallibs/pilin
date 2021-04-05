package io.smallibs.pilin.control

import io.smallibs.pilin.type.App

object Monad {

    interface Core<F> {
        suspend fun <A> returns(a: A): App<F, A>
        suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A>
        suspend fun <A, B> bind(ma: App<F, A>): suspend (suspend (A) -> App<F, B>) -> App<F, B>
    }

    interface WithJoin<F> {
        suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A>
    }

    interface WithBindAndReturn<F> {
        suspend fun <A> returns(a: A): App<F, A>
        suspend fun <A, B> bind(ma: App<F, A>): suspend (suspend (A) -> App<F, B>) -> App<F, B>
    }

    class Infix<F>(private val w: WithBindAndReturn<F>) {
        suspend infix fun <A, B> App<F, A>.bind(f: suspend (A) -> App<F, B>): App<F, B> = w.bind<A, B>(this)(f)
    }

    interface API<F> : WithJoin<F>, WithBindAndReturn<F> {
        val applicative: Applicative.API<F>
        val infix: Infix<F> get() = Infix(this)

        override suspend fun <A> returns(a: A): App<F, A> = applicative.pure(a)

        override suspend fun <A, B> bind(ma: App<F, A>): suspend (suspend (A) -> App<F, B>) -> App<F, B> =
            { f ->
                join(applicative.map(f)(ma))
            }
    }

}

