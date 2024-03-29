package io.smallibs.pilin.standard.option

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.comprehension.Comprehension
import io.smallibs.pilin.core.Standard.id
import io.smallibs.pilin.standard.option.Applicative.applicative
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.OptionK
import io.smallibs.pilin.standard.option.Option.OptionK.fold
import io.smallibs.pilin.standard.option.comprehension.OptionComprehension
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl(applicative: Applicative.API<OptionK>) : Monad.API<OptionK>,
        Monad.WithReturnsMapAndJoin<OptionK>, Monad.ViaApplicative<OptionK>(applicative) {
        override suspend fun <A> join(mma: App<OptionK, App<OptionK, A>>): App<OptionK, A> = mma.fold(::none, ::id)

        private class Do : Monad.Do<OptionK>(monad) {
            override suspend infix operator fun <A> invoke(f: suspend Comprehension<OptionK>.() -> A): App<OptionK, A> =
                OptionComprehension.run(this.c, f)
        }

        override val `do`: Monad.Do<OptionK> get() = Do()

    }

    val monad: Monad.API<OptionK> = MonadImpl(applicative)
}