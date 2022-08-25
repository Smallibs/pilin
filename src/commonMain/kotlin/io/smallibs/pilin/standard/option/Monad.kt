package io.smallibs.pilin.standard.option

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.core.Standard.id
import io.smallibs.pilin.standard.option.Applicative.applicative
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.OptionK
import io.smallibs.pilin.standard.option.Option.OptionK.fold
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl(applicative: Applicative.API<OptionK>) : Monad.API<OptionK>,
        Monad.WithReturnsMapAndJoin<OptionK>, Monad.ViaApplicative<OptionK>(applicative) {
        override suspend fun <A> join(mma: App<OptionK, App<OptionK, A>>): App<OptionK, A> = mma.fold(::none, ::id)
    }

    val monad: Monad.API<OptionK> = MonadImpl(applicative)
}