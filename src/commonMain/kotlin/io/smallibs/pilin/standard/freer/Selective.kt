package io.smallibs.pilin.standard.freer

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.freer.Freer.FreerK
import io.smallibs.pilin.standard.freer.Monad.monad

object Selective {
    private class SelectiveImpl<F>(monad: Monad.API<FreerK<F>>) :
        Selective.API<FreerK<F>>, Selective.ViaMonad<FreerK<F>>(monad)

    fun <F> selective(): Selective.API<FreerK<F>> = SelectiveImpl(monad())
}