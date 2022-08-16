package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.identity.Identity.IdentityK

object Selective {
    private class SelectiveImpl(monad: io.smallibs.pilin.abstractions.Monad.API<IdentityK>) :
        Selective.ViaMonad<IdentityK>(monad)

    val selective: Selective.API<IdentityK> = SelectiveImpl(Monad.monad)
}