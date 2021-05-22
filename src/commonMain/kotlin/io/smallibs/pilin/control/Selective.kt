package io.smallibs.pilin.control

import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.core.Standard.curry
import io.smallibs.pilin.standard.either.Either.Companion.functor
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.either.Either.TK
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
                val a = map<App<TK<A>, B>, App<TK<A>, App<TK<B>, C>>>(functor<A>().map { left(it) })(e)
                val b = map<Fun<A, C>, Fun<A, App<TK<B>, C>>> { f -> { right(f(it)) } }(l)
                select(select(a)(b))(r)
            }
    }

    interface WithBranch<F> : Core<F> {
        override suspend fun <A, B> select(e: App<F, App<TK<A>, B>>): Fun<App<F, Fun<A, B>>, App<F, B>> = { r ->
            branch<A, B, B>(e)(r)(pure(Standard::id))
        }
    }



    interface API<F> : Core<F>

}