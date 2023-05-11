package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

interface Continuation<out I> : App<Continuation.ContinuationK, I> {

    // method with `O` as an existential type
    suspend operator fun <O> invoke(k: Fun<I, O>): O

    object ContinuationK {
        val <I> App<ContinuationK, I>.fix: Continuation<I>
            get() = this as Continuation<I>

        suspend operator fun <I, O> App<ContinuationK, I>.invoke(k: Fun<I, O>): O = this.fix(k)
    }

    companion object {
        val functor = Functor.functor()
        val applicative = Applicative.applicative()
        val selective = Selective.selective()
        val monad = Monad.monad()
    }
}
