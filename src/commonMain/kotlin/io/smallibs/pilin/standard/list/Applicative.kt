package io.smallibs.pilin.standard.list

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.standard.list.List.ListK
import io.smallibs.pilin.standard.list.List.ListK.Companion.fix
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl : Applicative.API<ListK>, Applicative.WithPureAndApply<ListK> {
        override suspend fun <A> pure(a: A): App<ListK, A> = List(a)

        override suspend fun <A, B> apply(mf: App<ListK, Fun<A, B>>): Fun<App<ListK, A>, App<ListK, B>> = { ml ->
            val r: MutableList<B> = mutableListOf()
            for (f in mf.fix) {
                for (a in ml.fix) {
                    r += f(a)
                }
            }
            List(r)
        }

    }

    val applicative: Applicative.API<ListK> = ApplicativeImpl()
}