package io.smallibs.pilin.standard.option

import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.option.Option.OptionK

object Selective {
    private class SelectiveImpl(monad: io.smallibs.pilin.abstractions.Monad.API<OptionK>) :
        Selective.ViaMonad<OptionK>(monad)

    val selective: Selective.ViaMonad<OptionK> = SelectiveImpl(Monad.monad)
}