package io.smallibs.pilin.effect

import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.effect.And.Companion.and
import io.smallibs.pilin.effect.Effects.Companion.handle
import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class CombinedEffectTest {
    private class State<F>(
        val get: App<F, String>,
        val set: (String) -> App<F, Unit>,
    ) : Handler

    private class IOConsole<F>(
        val printString: (String) -> App<F, Unit>,
        val readString: App<F, String>,
    ) : Handler

    private fun <F> effects(monad: Monad.API<F>): Effects<And<State<F>, IOConsole<F>>, App<F, Unit>> =
        handle { (state, console) ->
            with(monad.infix) {
                console.readString bind {
                    state.set(it)
                } bind {
                    state.get
                } bind {
                    console.printString("Hello $it")
                }
            }
        }

    private fun state(): State<ContinuationK> {
        var state = ""

        return State(
            get = continuation<String, List<String>> { k ->
                listOf("get()") + k(state)
            },
            set = { value ->
                continuation<Unit, List<String>> { k ->
                    state = value
                    listOf("set($value)") + k(Unit)
                }
            }
        )
    }

    private fun console(): IOConsole<ContinuationK> =
        IOConsole(
            printString = { text ->
                continuation<Unit, List<String>> { k ->
                    listOf("printString($text)") + k(Unit)
                }
            },
            readString = continuation<String, List<String>> { k ->
                listOf("readStream(World)") + k("World")
            }
        )

    @Test
    fun shouldPerformEffect() {
        val handled = effects(Continuation.monad) with {
            state() and console()
        }

        val traces = runBlocking { (handled()).invoke<Unit, List<String>> { listOf() } }

        assertEquals(listOf("readStream(World)", "set(World)", "get()", "printString(Hello World)"), traces)
    }
}