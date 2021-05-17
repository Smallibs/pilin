package io.smallibs.pilin.effect

import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.effect.And.Companion.and
import io.smallibs.pilin.effect.Effects.Companion.handle
import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.TK
import io.smallibs.pilin.standard.continuation.Continuation.TK.Companion.invoke
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

    private fun state(): State<TK<List<String>>> {
        var state = ""

        return State(
            get = continuation { k ->
                listOf("get()") + k(state)
            },
            set = { value ->
                continuation { k ->
                    state = value
                    listOf("set($value)") + k(Unit)
                }
            }
        )
    }

    private fun console(): IOConsole<TK<List<String>>> =
        IOConsole(
            printString = { text ->
                continuation { k ->
                    listOf("printString($text)") + k(Unit)
                }
            },
            readString = continuation { k ->
                listOf("readStream(World)") + k("World")
            }
        )

    @Test
    fun shouldPerformEffect() {
        val handled = effects<TK<List<String>>>(Continuation.monad()) with {
            state() and console()
        }

        val traces = runBlocking { (handled()) { listOf() } }

        assertEquals(listOf("readStream(World)", "set(World)", "get()", "printString(Hello World)"), traces)
    }
}