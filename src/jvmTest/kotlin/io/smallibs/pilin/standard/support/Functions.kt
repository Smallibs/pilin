package io.smallibs.pilin.standard.support

import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functions {

    val intToString: Fun<Int, String> = { i -> i.toString() }

    val stringToInt: Fun<String, Int> = { i -> i.toInt() }

    suspend fun <F, A, B> ret(f: Fun<A, B>): Fun<Fun<B, App<F, B>>, Fun<A, App<F, B>>> =
        Standard.curry { r, a -> r(f(a)) }

    suspend fun <F> retStr(r: Fun<String, App<F, String>>): Fun<Int, App<F, String>> = ret<F, Int, String>(intToString)(r)

    suspend fun <F> retInt(r: Fun<Int, App<F, Int>>): Fun<String, App<F, Int>> = ret<F, String, Int>(stringToInt)(r)

}