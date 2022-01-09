package io.smallibs.pilin.standard.either

import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.either.Either.EitherK

object Selective {
    private class SelectiveImpl<O>(monad: io.smallibs.pilin.abstractions.Monad.API<EitherK<O>>) :
        Selective.ViaMonad<EitherK<O>>(monad)

    fun <O> selective(): Selective.ViaMonad<EitherK<O>> = SelectiveImpl(Monad.monad())
}