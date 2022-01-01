package io.smallibs.pilin.control

import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.core.Standard.curry
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {

    interface Core<F> : Functor.Core<F> {
        suspend fun <A> pure(a: A): App<F, A>
        suspend fun <A, B> product(ma: App<F, A>): Fun<App<F, B>, App<F, Pair<A, B>>>
        suspend fun <A, B> apply(mf: App<F, Fun<A, B>>): Fun<App<F, A>, App<F, B>>
    }

    interface WithPureMapAndProduct<F> : Core<F> {
        override suspend fun <A, B> apply(mf: App<F, Fun<A, B>>): Fun<App<F, A>, App<F, B>> =
            product<Fun<A, B>, A>(mf) then map { p -> p.first(p.second) }
    }

    interface WithPureAndApply<F> : Core<F> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<F, A>, App<F, B>> =
            apply(pure(f))

        override suspend fun <A, B> product(ma: App<F, A>): Fun<App<F, B>, App<F, Pair<A, B>>> =
            apply(apply(pure(curry { a: A, b: B -> a to b }))(ma))
    }

    abstract class ViaFunctor<F>(functor: Functor.Core<F>) : Core<F>, Functor.Core<F> by functor

    open class Operation<F>(private val c: Core<F>) : Core<F> by c {
        suspend fun <A, B> lift1(f: Fun<A, B>): Fun<App<F, A>, App<F, B>> =
            map(f)

        suspend fun <A, B, C> lift2(f: Fun<A, Fun<B, C>>): Fun<App<F, A>, Fun<App<F, B>, App<F, C>>> =
            apply(pure(f)) then ::apply

        suspend fun <A, B, C, D> lift3(f: Fun<A, Fun<B, Fun<C, D>>>): Fun<App<F, A>, Fun<App<F, B>, Fun<App<F, C>, App<F, D>>>> =
            { ma -> lift2(f)(ma) then ::apply }

        suspend fun <A, B> discardLeft(ma: App<F, A>): Fun<App<F, B>, App<F, B>> =
            lift2<A, B, B>(curry { _, y -> y })(ma)

        suspend inline fun <A, B> discardRight(ma: App<F, A>): Fun<App<F, B>, App<F, A>> =
            lift2<A, B, A>(Standard::const)(ma)
    }

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    open class Infix<F>(private val c: Core<F>) : Functor.Infix<F>(c), Core<F> by c {
        private val o = Operation(c)

        suspend infix operator fun <A, B> App<F, A>.times(mb: App<F, B>): App<F, Pair<A, B>> =
            c.product<A, B>(this)(mb)

        suspend inline infix fun <A, B> App<F, A>.product(mb: App<F, B>): App<F, Pair<A, B>> =
            this * mb

        suspend infix fun <A, B> App<F, Fun<A, B>>.apply(ma: App<F, A>): App<F, B> =
            c.apply(this)(ma)

        suspend infix fun <A, B> App<F, A>.discardLeft(mb: App<F, B>): App<F, B> =
            o.discardLeft<A, B>(this)(mb)

        suspend infix fun <A, B> App<F, A>.discardRight(mb: App<F, B>): App<F, A> =
            o.discardRight<A, B>(this)(mb)

    }

    interface API<F> : Core<F> {
        val operation: Operation<F> get() = Operation(this)
        val infix: Infix<F> get() = Infix(this)
    }

}