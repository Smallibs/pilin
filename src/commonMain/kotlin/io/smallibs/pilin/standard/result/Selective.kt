package io.smallibs.pilin.standard.result

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Selective
import io.smallibs.pilin.standard.result.Result.Companion.monad
import io.smallibs.pilin.standard.result.Result.ResultK

object Selective {
    private class SelectiveImpl<E>(monad: Monad.API<ResultK<E>>) : Selective.ViaMonad<ResultK<E>>(monad)

    fun <E> selective(): Selective.API<ResultK<E>> = SelectiveImpl(monad())
}