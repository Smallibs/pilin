package io.smallibs.pilin.standard.either

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.either.Either.EitherK
import io.smallibs.pilin.standard.either.Monad.monad

object Selective {
    private class SelectiveImpl<O>(monad: Monad.API<EitherK<O>>) :
        Selective.ViaMonad<EitherK<O>>(monad)

    fun <O> selective(): Selective.API<EitherK<O>> = SelectiveImpl(monad())
}