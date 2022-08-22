package io.smallibs.pilin.standard.state

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.state.Monad.monad
import io.smallibs.pilin.standard.state.State.StateK

object Selective {
    private class SelectiveImpl<F, S>(inner: Monad.Core<F>) : Selective.ViaMonad<StateK<F, S>>(monad(inner))

    fun <F, S> selective(m: Monad.Core<F>): Selective.API<StateK<F, S>> = SelectiveImpl(m)
}