package io.smallibs.pilin.standard.support

import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functions {

    suspend fun <F, A, B> ret(f: Fun<A, B>): Fun<Fun<B, App<F, B>>, Fun<A, App<F, B>>> =
        Standard.curry { r, a -> r(f(a)) }

}