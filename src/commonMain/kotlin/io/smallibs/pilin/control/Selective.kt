package io.smallibs.pilin.control

import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.core.Standard.compose
import io.smallibs.pilin.core.Standard.curry
import io.smallibs.pilin.standard.either.Either.Companion.functor
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.either.Either.TK
import io.smallibs.pilin.standard.either.Either.TK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Selective {

    interface Core<F> : Applicative.Core<F> {
        suspend fun <A, B> select(e: App<F, App<TK<A>, B>>): Fun<App<F, Fun<A, B>>, App<F, B>>
        suspend fun <A, B, C> branch(e: App<F, App<TK<A>, B>>): Fun<App<F, Fun<A, C>>, Fun<App<F, Fun<B, C>>, App<F, C>>>
    }

    interface WithSelect<F> : Core<F> {
        override suspend fun <A, B, C> branch(e: App<F, App<TK<A>, B>>): Fun<App<F, Fun<A, C>>, Fun<App<F, Fun<B, C>>, App<F, C>>> =
            curry { l, r ->
                val a = map(functor<A>().map<B, App<TK<B>, C>>(::left))(e)
                val b = map(compose<A, C, App<TK<B>, C>>(::right))(l)
                select(select(a)(b))(r)
            }
    }

    interface WithBranch<F> : Core<F> {
        override suspend fun <A, B> select(e: App<F, App<TK<A>, B>>): Fun<App<F, Fun<A, B>>, App<F, B>> = { r ->
            branch<A, B, B>(e)(r)(pure(Standard::id))
        }
    }

    open class ViaMonad<F>(private val monad: Monad.Core<F>) : Core<F>,
        WithSelect<F>,
        Monad.Core<F> by monad {
        override suspend fun <A, B> select(e: App<F, App<TK<A>, B>>): Fun<App<F, Fun<A, B>>, App<F, B>> = { f ->
            bind { e: App<TK<A>, B> -> e.fold({ a -> map { f: Fun<A, B> -> f(a) }(f) }, ::pure) }(e)
        }
    }

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    class Operation<F>(private val c: Core<F>) : Applicative.Operation<F>(c), Core<F> by c {
        suspend fun <A> if_(predicate: App<F, Boolean>): Fun<App<F, A>, Fun<App<F, A>, App<F, A>>> =
            curry { ifTrue, ifFalse ->
                val bTrue = map<A, Fun<Unit, A>>(Standard::const)(ifTrue)
                val bFalse = map<A, Fun<Unit, A>>(Standard::const)(ifFalse)
                val test = map<Boolean, App<TK<Unit>, Unit>> { if (it) left(Unit) else right(Unit) }

                branch<Unit, Unit, A>(test((predicate)))(bTrue)(bFalse)
            }

        suspend fun <A> bindBool(test: App<F, Boolean>): Fun<Fun<Boolean, App<F, A>>, App<F, A>> =
            { if_<A>(test)(it(true))(it(false)) }

        suspend fun when_(predicate: App<F, Boolean>): Fun<App<F, Unit>, App<F, Unit>> =
            { if_<Unit>(predicate)(it)(pure(Unit)) }

        suspend fun or(left: App<F, Boolean>): Fun<App<F, Boolean>, App<F, Boolean>> =
            { if_<Boolean>(left)(pure(true))(it) }

        suspend fun and(left: App<F, Boolean>): Fun<App<F, Boolean>, App<F, Boolean>> =
            { if_<Boolean>(pure(false))(left)(it) }

        suspend fun <A> exists(predicate: Fun<A, App<F, Boolean>>): Fun<List<A>, App<F, Boolean>> =
            { list ->
                suspend fun exists(index: Int): App<F, Boolean> =
                    if (index < list.size)
                        if_<Boolean>(predicate(list[index]))(pure(true))(exists(index + 1))
                    else
                        pure(false)

                exists(0)
            }

        suspend fun <A> forall(predicate: Fun<A, App<F, Boolean>>): Fun<List<A>, App<F, Boolean>> =
            { list ->
                suspend fun forall(index: Int): App<F, Boolean> =
                    if (index < list.size)
                        if_<Boolean>(predicate(list[index]))(forall(index + 1))(pure(false))
                    else
                        pure(true)

                forall(0)
            }
    }

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    class Infix<F>(private val c: Core<F>) : Applicative.Infix<F>(c), Core<F> by c {
        private val o = Operation(c)

        suspend infix fun <A, B, C> App<F, App<TK<A>, B>>.branch(l: App<F, Fun<A, C>>): Fun<App<F, Fun<B, C>>, App<F, C>> =
            c.branch<A, B, C>(this)(l)

        suspend infix fun <A, B> App<F, App<TK<A>, B>>.select(r: App<F, Fun<A, B>>): App<F, B> =
            c.select(this)(r)

        suspend infix fun App<F, Boolean>.or(right: App<F, Boolean>): App<F, Boolean> =
            o.or(this)(right)

        suspend infix fun App<F, Boolean>.and(right: App<F, Boolean>): App<F, Boolean> =
            o.and(this)(right)

        suspend infix fun <A> Fun<A, App<F, Boolean>>.exists(list: List<A>): App<F, Boolean> =
            o.exists(this)(list)

        suspend infix fun <A> Fun<A, App<F, Boolean>>.forall(list: List<A>): App<F, Boolean> =
            o.forall(this)(list)
    }

    interface API<F> : Core<F> {
        val operation: Operation<F> get() = Operation(this)
        val infix: Infix<F> get() = Infix(this)
    }

}