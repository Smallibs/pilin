package io.smallibs.pilin.effect

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.effect.And.Companion.and
import io.smallibs.pilin.effect.Effects.Companion.handle
import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.monad
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.utils.runTest
import org.junit.Test

import kotlin.test.assertEquals

class CombinedEffectTest {

    private class State<F>(
        val get: App<F, String>,
        val set: (String) -> App<F, Unit>,
    ) : EffectHandler

    private class Console<F>(
        val printString: (String) -> App<F, Unit>,
        val readString: App<F, String>,
    ) : EffectHandler

    private fun <F> effects(monad: Monad.API<F>): Effects<And<State<F>, Console<F>>, App<F, Unit>> =
        handle { (state, console) ->
            monad `do` {
                console.readString.bind().let {
                    state.set(it).bind()
                }
                state.get.bind().let {
                    console.printString("Hello $it").bind()
                }
            }
        }

    private fun state(traces: MutableList<String>): State<ContinuationK> {
        var state = ""

        return State(
            set = { value ->
                object : Continuation<Unit> {
                    override suspend fun <O> invoke(k: Fun<Unit, O>): O {
                        state = value
                        traces.add("set($value)")
                        return k(Unit)
                    }
                }
            },
            get = object : Continuation<String> {
                override suspend fun <O> invoke(k: Fun<String, O>): O {
                    traces.add("get()")
                    return k(state)
                }
            }
        )
    }

    private fun console(traces: MutableList<String>): Console<ContinuationK> =
        Console(
            printString = { text ->
                object : Continuation<Unit> {
                    override suspend fun <O> invoke(k: Fun<Unit, O>): O {
                        traces.add("printString($text)")
                        return k(Unit)
                    }
                }
            },
            readString = object : Continuation<String> {
                override suspend fun <O> invoke(k: Fun<String, O>): O {
                    traces.add("readStream(World)")
                    return k("World")
                }
            }
        )

    @Test
    fun shouldPerformEffect() {
        val traces = mutableListOf<String>()
        val handler = state(traces) and console(traces)
        val handled = effects(monad) with handler

        runTest { (handled()).invoke { } }

        assertEquals(listOf("readStream(World)", "set(World)", "get()", "printString(Hello World)"), traces)
    }
}