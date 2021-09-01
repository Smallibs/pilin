package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.control.Selective
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK

object Selective {
    private class SelectiveImpl(monad: io.smallibs.pilin.control.Monad.API<ContinuationK>) :
        Selective.API<ContinuationK>,
        Selective.ViaMonad<ContinuationK>(monad) {
    }

    val selective: Selective.API<ContinuationK> = SelectiveImpl(Monad.monad)
}