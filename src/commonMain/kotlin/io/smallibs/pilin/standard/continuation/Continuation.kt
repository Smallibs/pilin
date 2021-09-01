package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

data class Continuation<I>(private val behavior: C<I>) : App<Continuation.ContinuationK, I> {
    interface C<I> {
        suspend fun <O> get(): Fun<Fun<I, O>, O>
    }

    suspend operator fun <O> invoke(a: Fun<I, O>) = behavior.get<O>()(a)

    class ContinuationK private constructor() {
        companion object {
            val <I> App<ContinuationK, I>.fix: Continuation<I> get() = this as Continuation<I>

            suspend operator fun <A, B> App<ContinuationK, A>.invoke(a: Fun<A, B>): B =
                this.fix(a)
        }
    }

    companion object {
        fun <I, O> continuation(behavior: Fun<Fun<I, O>, O>): Continuation<I> = Continuation(object : C<I> {
            @Suppress("UNCHECKED_CAST")
            override suspend fun <O> get(): Fun<Fun<I, O>, O> = behavior as Fun<Fun<I, O>, O> /* UNSAFE */
        })

        val functor = Functor.functor
        val applicative = Applicative.applicative
        val selective = Selective.selective
        val monad = Monad.monad
    }
}
