package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.identity.Identity.IdentityK
import io.smallibs.pilin.standard.identity.Monad.monad

object Selective {
    private class SelectiveImpl(monad: Monad.API<IdentityK>) :
        Selective.ViaMonad<IdentityK>(monad)

    val selective: Selective.API<IdentityK> = SelectiveImpl(monad)
}