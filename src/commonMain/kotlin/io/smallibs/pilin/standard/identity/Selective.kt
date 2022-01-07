package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.control.Selective
import io.smallibs.pilin.standard.identity.Identity.IdentityK

object Selective {
    private class SelectiveImpl(monad: io.smallibs.pilin.control.Monad.API<IdentityK>) :
        Selective.ViaMonad<IdentityK>(monad)

    val selective: Selective.ViaMonad<IdentityK> = SelectiveImpl(Monad.monad)
}