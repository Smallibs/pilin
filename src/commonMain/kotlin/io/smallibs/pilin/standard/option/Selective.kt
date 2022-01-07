package io.smallibs.pilin.standard.option

import io.smallibs.pilin.control.Selective
import io.smallibs.pilin.standard.option.Option.OptionK

object Selective {
    private class SelectiveImpl(monad: io.smallibs.pilin.control.Monad.API<OptionK>) :
        Selective.ViaMonad<OptionK>(monad)

    val selective: Selective.ViaMonad<OptionK> = SelectiveImpl(Monad.monad)
}