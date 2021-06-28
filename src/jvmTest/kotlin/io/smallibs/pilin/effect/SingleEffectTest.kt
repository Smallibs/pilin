package io.smallibs.pilin.effect

import io.smallibs.pilin.effect.Effects.Companion.handle
import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.monad
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class SingleEffectTest {
    private class IOConsole<R>(
        val printString: (String) -> Continuation<Unit, R>,
        val readString: Continuation<String, R>,
    ) : Handler

    private fun <R> effects(): Effects<IOConsole<R>, App<ContinuationK<R>, Unit>> = handle { console ->
        with(monad<R>().infix) {
            console.readString bind { value ->
                console.printString("Hello $value")
            }
        }
    }

    private fun console(): IOConsole<List<String>> =
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
        val handled = effects<List<String>>() with console()

        val traces = runBlocking { (handled()) { listOf() } }

        assertEquals(listOf("readStream(World)", "printString(Hello World)"), traces)
    }
}