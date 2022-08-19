package io.smallibs.pilin.abstractions

import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.type.App2
import io.smallibs.pilin.type.Fun

object ProFunctor {

    interface WithDiMap<F> {
        suspend fun <A, B, C, D> diMap(f: Fun<A, B>): Fun<Fun<C, D>, Fun<App2<F, B, C>, App2<F, A, D>>>
    }

    interface WithContraMapFstAndMapSnd<F> {
        suspend fun <A, B, C> contraMapFst(f: Fun<A, B>): Fun<App2<F, B, C>, App2<F, A, C>>
        suspend fun <A, B, C> mapSnd(f: Fun<B, C>): Fun<App2<F, A, B>, App2<F, A, C>>
    }

    interface Core<F> : WithDiMap<F>, WithContraMapFstAndMapSnd<F>

    class ViaDiMap<F>(private val inner: WithDiMap<F>) : WithDiMap<F> by inner, API<F> {
        override suspend fun <A, B, C> contraMapFst(f: Fun<A, B>): Fun<App2<F, B, C>, App2<F, A, C>> = {
            diMap<A, B, C, C>(f)(Standard::id)(it)
        }

        override suspend fun <A, B, C> mapSnd(f: Fun<B, C>): Fun<App2<F, A, B>, App2<F, A, C>> = {
            diMap<A, A, B, C>(Standard::id)(f)(it)
        }
    }

    class ViaContraMapFstAndMapSnd<F>(private val inner: WithContraMapFstAndMapSnd<F>) :
        WithContraMapFstAndMapSnd<F> by inner, API<F> {
        override suspend fun <A, B, C, D> diMap(f: Fun<A, B>): Fun<Fun<C, D>, Fun<App2<F, B, C>, App2<F, A, D>>> =
            { g ->
                {
                    contraMapFst<A, B, D>(f)(mapSnd<B, C, D>(g)(it))
                }
            }
    }

    class Infix<F>(private val c: Core<F>) : Core<F> by c {
        suspend infix fun <A, B, C> Fun<A, B>.contraMapFst(f: App2<F, B, C>): App2<F, A, C> =
            c.contraMapFst<A, B, C>(this)(f)

        suspend infix fun <A, B, C> Fun<B, C>.mapSnd(f: App2<F, A, B>): App2<F, A, C> = c.mapSnd<A, B, C>(this)(f)
    }

    interface API<F> : Core<F> {
        val infix: Infix<F> get() = Infix(this)
    }

}