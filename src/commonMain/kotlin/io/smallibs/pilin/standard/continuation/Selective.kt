package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Monad.monad

object Selective {
    private class SelectiveImpl(monad: Monad.API<ContinuationK>) :
        Selective.API<ContinuationK>, Selective.ViaMonad<ContinuationK>(monad)

    fun selective(): Selective.API<ContinuationK> = SelectiveImpl(monad())
}