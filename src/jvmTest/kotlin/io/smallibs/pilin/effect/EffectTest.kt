package io.smallibs.pilin.effect

import io.smallibs.pilin.effect.Effects.Companion.handle
import io.smallibs.pilin.extension.Comprehension.Companion.`do`
import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.monad
import io.smallibs.pilin.standard.continuation.Continuation.TK
import io.smallibs.pilin.standard.continuation.Continuation.TK.Companion.invoke
import io.smallibs.pilin.type.App
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class EffectTest {
    private class IOConsole<R>(
        val printString: (String) -> Continuation<Unit, R>,
        val readString: Continuation<String, R>,
    ) : Handler

    private fun <R> effects(): Effects<IOConsole<R>, App<TK<R>, Unit>> = handle { console ->
        monad<R>() `do` {
            val (value) = console.readString
            val (any) = console.printString("Hello $value")
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