package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

data class Continuation<I, O>(private val behavior: Fun<Fun<I, O>, O>) : App<Continuation.TK<O>, I> {
    suspend operator fun invoke(a: Fun<I, O>) = behavior(a)

    class TK<O> private constructor() {
        companion object {
            val <I, O> App<TK<O>, I>.fix: Continuation<I, O> get() = this as Continuation<I, O>

            suspend operator fun <A, B> App<TK<B>, A>.invoke(a: Fun<A, B>): B =
                this.fix(a)
        }
    }

    companion object {
        fun <I, O> continuation(behavior: Fun<Fun<I, O>, O>): Continuation<I, O> = Continuation(behavior)

        fun <O> functor() = Functor.functor<O>()
        fun <O> applicative() = Applicative.applicative<O>()
        fun <O> monad() = Monad.monad<O>()
    }
}
