package io.smallibs.pilin.standard.free.monad

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.free.monad.Free.FreeK
import io.smallibs.pilin.standard.free.monad.Monad.monad

object Selective {
    private class SelectiveImpl<F>(inner: Functor.Core<F>) :
        Selective.API<FreeK<F>>, Selective.ViaMonad<FreeK<F>>(monad(inner))

    fun <F> selective(inner: Functor.Core<F>): Selective.API<FreeK<F>> = SelectiveImpl(inner)
}