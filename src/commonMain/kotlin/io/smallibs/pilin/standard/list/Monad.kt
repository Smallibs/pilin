package io.smallibs.pilin.standard.list

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.standard.list.Applicative.applicative
import io.smallibs.pilin.standard.list.List.ListK
import io.smallibs.pilin.standard.list.List.ListK.Companion.fix
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl(applicative: Applicative.API<ListK>) : Monad.API<ListK>, Monad.WithReturnsMapAndJoin<ListK>,
        Monad.ViaApplicative<ListK>(applicative) {
        override suspend fun <A> join(mma: App<ListK, App<ListK, A>>): App<ListK, A> {
            val r: MutableList<A> = mutableListOf()
            for (a in mma.fix) {
                r.addAll(a.fix)
            }
            return List(r)
        }

    }

    val monad: Monad.API<ListK> = MonadImpl(applicative)
}