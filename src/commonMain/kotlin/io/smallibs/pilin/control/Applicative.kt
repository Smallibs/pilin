package io.smallibs.pilin.control

import io.smallibs.pilin.core.Fun.curry
import io.smallibs.pilin.type.App

object Applicative {

    interface Core<F> {
        suspend fun <A> pure(a: A): App<F, A>
        suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<F, A>) -> App<F, B>
        suspend fun <A, B> product(ma: App<F, A>): suspend (mb: App<F, B>) -> App<F, Pair<A, B>>
        suspend fun <A, B> apply(mf: App<F, suspend (A) -> B>): suspend (App<F, A>) -> App<F, B>
    }

    interface WithPureMapAndProduct<F> : Core<F> {
        override suspend fun <A, B> apply(mf: App<F, suspend (A) -> B>): suspend (App<F, A>) -> App<F, B> =
            { ma ->
                map<Pair<suspend (A) -> B, A>, B> { p ->
                    p.first(p.second)
                }(product<suspend (A) -> B, A>(mf)(ma))
            }
    }

    interface WithPureAndApply<F> : Core<F> {
        override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<F, A>) -> App<F, B> =
            apply(pure(f))

        override suspend fun <A, B> product(ma: App<F, A>): suspend (mb: App<F, B>) -> App<F, Pair<A, B>> =
            apply(apply(pure(curry { a: A, b: B -> a to b }))(ma))
    }

    class Operation<F>(private val c: Core<F>) {
        suspend fun <A, B> lift1(f: suspend (A) -> B): suspend (App<F, A>) -> App<F, B> = c.map(f)

        suspend fun <A, B, C> lift2(f: suspend (A) -> suspend (B) -> C): suspend (App<F, A>) -> suspend (App<F, B>) -> App<F, C> =
            { ma -> { mb -> c.apply(c.apply(c.pure(f))(ma))(mb) } }

        suspend fun <A, B, C, D> lift3(f: suspend (A) -> suspend (B) -> suspend (C) -> D): suspend (App<F, A>) -> suspend (App<F, B>) -> suspend (App<F, C>) -> App<F, D> =
            { ma -> { mb -> { mc -> c.apply(lift2(f)(ma)(mb))(mc) } } }
    }

    class Infix<F>(private val c: Core<F>) {
        suspend infix fun <A, B> App<F, suspend (A) -> B>.apply(ma: App<F, A>): App<F, B> = c.apply(this)(ma)
    }

    interface API<F> : Core<F> {
        val operation: Operation<F> get() = Operation(this)
        val infix: Infix<F> get() = Infix(this)
    }

}