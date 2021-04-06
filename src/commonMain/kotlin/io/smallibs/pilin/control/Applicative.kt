package io.smallibs.pilin.control

import io.smallibs.pilin.core.Fun.curry
import io.smallibs.pilin.type.App

object Applicative {

    interface Core<F> : Functor.Core<F> {
        suspend fun <A> pure(a: A): App<F, A>
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

    interface ViaFunctor<F> : Core<F> {
        val functor: Functor.Core<F>

        override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<F, A>) -> App<F, B> = functor.map(f)
    }

    class Operation<F>(private val c: Core<F>) : Core<F> by c {
        suspend fun <A, B> lift1(f: suspend (A) -> B): suspend (App<F, A>) -> App<F, B> =
            map(f)

        suspend fun <A, B, C> lift2(f: suspend (A) -> suspend (B) -> C): suspend (App<F, A>) -> suspend (App<F, B>) -> App<F, C> =
            { ma -> { mb -> apply(apply(pure(f))(ma))(mb) } }

        suspend fun <A, B, C, D> lift3(f: suspend (A) -> suspend (B) -> suspend (C) -> D): suspend (App<F, A>) -> suspend (App<F, B>) -> suspend (App<F, C>) -> App<F, D> =
            { ma -> { mb -> { mc -> apply(lift2(f)(ma)(mb))(mc) } } }

    }

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    open class Infix<F>(private val c: Core<F>) : Functor.Infix<F>(c), Core<F> by c {
        suspend infix fun <A, B> App<F, suspend (A) -> B>.apply(ma: App<F, A>): App<F, B> = c.apply(this)(ma)
    }

    interface API<F> : Core<F> {
        val operation: Operation<F> get() = Operation(this)
        val infix: Infix<F> get() = Infix(this)
    }

}