package io.smallibs.pilin.standard.`try`

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.standard.`try`.Try.Companion.failure
import io.smallibs.pilin.standard.`try`.Try.Companion.success
import io.smallibs.pilin.standard.`try`.Try.TryK
import io.smallibs.pilin.standard.`try`.Try.TryK.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl : Functor.API<TryK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<TryK, A>, App<TryK, B>> =
            { ma -> ma.fold(::failure, f then ::success) }
    }

    val functor: Functor.API<TryK> = FunctorImpl()
}