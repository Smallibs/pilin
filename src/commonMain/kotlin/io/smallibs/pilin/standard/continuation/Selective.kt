package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.control.Selective
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK

object Selective {
    private class SelectiveImpl<O>(monad: io.smallibs.pilin.control.Monad.API<ContinuationK<O>>) :
        Selective.API<ContinuationK<O>>,
        Selective.ViaMonad<ContinuationK<O>>(monad) {
    }

    fun <O> selective(): Selective.API<ContinuationK<O>> = SelectiveImpl(Monad.monad())
}