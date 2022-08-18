package io.smallibs.pilin.standard.list

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.standard.list.List.ListK
import io.smallibs.pilin.standard.list.List.ListK.Companion.run
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl : Functor.API<ListK> {

        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<ListK, A>, App<ListK, B>> = { ml ->
            val r: MutableList<B> = mutableListOf()
            for (a in ml.run) {
                r += f(a)
            }
            List(r)
        }

    }

    val functor: Functor.API<ListK> = FunctorImpl()
}