package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK

object Selective {
    private class SelectiveImpl(monad: io.smallibs.pilin.abstractions.Monad.API<ContinuationK>) :
        Selective.API<ContinuationK>, Selective.ViaMonad<ContinuationK>(monad)

    fun selective(): Selective.API<ContinuationK> = SelectiveImpl(Monad.monad())
}