package io.smallibs.pilin.standard.writer

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Monoid
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.writer.Monad.monad

object Selective {
    private class SelectiveImpl<F, T>(inner: Monad.Core<F>, tape: Monoid.Core<T>) :
        Selective.ViaMonad<Writer.WriterK<F, T>>(monad(inner, tape))

    fun <F, T> selective(m: Monad.Core<F>, t: Monoid.Core<T>): Selective.API<Writer.WriterK<F, T>> = SelectiveImpl(m, t)
}