package io.smallibs.pilin.standard.list

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.list.Monad.monad

object Selective {
    private class SelectiveImpl(monad: Monad.API<List.ListK>) :
        Selective.ViaMonad<List.ListK>(monad)

    val selective: Selective.API<List.ListK> = SelectiveImpl(monad)
}