package io.smallibs.pilin.standard.option

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.option.Monad.monad
import io.smallibs.pilin.standard.option.Option.OptionK

object Selective {
    private class SelectiveImpl(monad: Monad.API<OptionK>) : Selective.ViaMonad<OptionK>(monad)

    val selective: Selective.API<OptionK> = SelectiveImpl(monad)
}