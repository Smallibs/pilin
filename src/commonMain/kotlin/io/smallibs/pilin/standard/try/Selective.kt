package io.smallibs.pilin.standard.`try`

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.`try`.Monad.monad
import io.smallibs.pilin.standard.`try`.Try.TryK

object Selective {
    private class SelectiveImpl(monad: Monad.API<TryK>) : Selective.ViaMonad<TryK>(monad)

    val selective: Selective.API<TryK> = SelectiveImpl(monad)
}