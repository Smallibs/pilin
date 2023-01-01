package io.smallibs.pilin.standard.either

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.comprehension.Comprehension
import io.smallibs.pilin.core.Standard.id
import io.smallibs.pilin.standard.either.Applicative.applicative
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.EitherK
import io.smallibs.pilin.standard.either.Either.EitherK.Companion.fold
import io.smallibs.pilin.standard.either.comprehension.EitherComprehension
import io.smallibs.pilin.type.App

object Monad {
    private class MonadImpl<L>(applicative: Applicative.API<EitherK<L>>) : Monad.API<EitherK<L>>,
        Monad.WithReturnsMapAndJoin<EitherK<L>>, Monad.ViaApplicative<EitherK<L>>(applicative) {
        override suspend fun <A> join(mma: App<EitherK<L>, App<EitherK<L>, A>>): App<EitherK<L>, A> =
            mma.fold(::left, ::id)

        private class Do<L> : Monad.Do<EitherK<L>>(monad()) {
            override suspend infix operator fun <A> invoke(f: suspend Comprehension<EitherK<L>>.() -> A): App<EitherK<L>, A> =
                EitherComprehension.run(this.c, f)
        }

        override val `do`: Monad.Do<EitherK<L>> get() = Do()
    }

    fun <L> monad(): Monad.API<EitherK<L>> = MonadImpl(applicative())
}