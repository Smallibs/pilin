package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.control.Selective
import io.smallibs.pilin.standard.identity.Identity.TK

object Selective {
    private class SelectiveImpl(monad: io.smallibs.pilin.control.Monad.API<TK>) :
        Selective.ViaMonad<TK>(monad) {
    }

    val selective: Selective.ViaMonad<TK> = SelectiveImpl(Monad.monad)
}