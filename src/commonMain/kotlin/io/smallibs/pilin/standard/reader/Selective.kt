package io.smallibs.pilin.standard.reader

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.reader.Reader.ReaderK

object Selective {
    private class SelectiveImpl<F, E>(inner: Monad.API<F>) :
        Selective.ViaMonad<ReaderK<F, E>>(Reader.monad(inner))

    fun <F, E> selective(m: Monad.API<F>): Selective.API<ReaderK<F, E>> =
        SelectiveImpl(m)
}