package io.smallibs.pilin.standard.option

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.option.Applicative.applicative
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.TK
import io.smallibs.pilin.standard.option.Option.TK.Companion.fold
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl(applicative: Applicative.API<TK>) :
        Monad.API<TK>,
        Monad.WithReturnsMapAndJoin<TK>,
        Monad.ViaApplicative<TK>(applicative) {
        override suspend fun <A> join(mma: App<TK, App<TK, A>>): App<TK, A> =
            mma.fold(::none) { it }
    }

    val monad: Monad.API<TK> = MonadImpl(applicative)
}