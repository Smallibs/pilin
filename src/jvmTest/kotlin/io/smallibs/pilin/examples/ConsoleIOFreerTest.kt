package io.smallibs.pilin.examples

import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.examples.ConsoleIOFreerTest.ConsoleIO.ConsoleIOK
import io.smallibs.pilin.examples.ConsoleIOFreerTest.ConsoleIO.ConsoleIOK.fix
import io.smallibs.pilin.standard.freer.monad.Freer
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.utils.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ConsoleIOFreerTest {

    private sealed interface ConsoleIO<A> : App<ConsoleIOK, A> {
        object ConsoleIOK {
            val <A> App<ConsoleIOK, A>.fix get() = this as ConsoleIO<A>
        }

        data class Tell<A>(val statement: String, val k: Fun<Unit, A>) : ConsoleIO<A>
        data class Ask<A>(val question: String, val k: Fun<String, A>) : ConsoleIO<A>
    }

    private fun Freer.Over<ConsoleIOK>.tell(statement: String): Freer<ConsoleIOK, Unit> =
        perform(ConsoleIO.Tell(statement, Standard::id))

    private fun Freer.Over<ConsoleIOK>.ask(request: String): Freer<ConsoleIOK, String> =
        perform(ConsoleIO.Ask(request, Standard::id))

    private fun <A, B> runConsoleIO(trace: MutableList<String>): Fun<Fun<B, A>, Fun<App<ConsoleIOK, B>, A>> = { f ->
        {
            when (val fa = it.fix) {
                is ConsoleIO.Ask -> {
                    trace += listOf("Ask ${fa.question}?")
                    f(fa.k("Hello"))
                }

                is ConsoleIO.Tell -> {
                    trace += listOf("Tell ${fa.statement}")
                    f(fa.k(Unit))
                }
            }
        }
    }

    private suspend fun <A> runConsole(trace: MutableList<String>): Freer.Handler<ConsoleIOK, A> =
        object : Freer.Handler<ConsoleIOK, A> {
            override suspend fun <B> handle(f: Fun<B, A>): Fun<App<ConsoleIOK, B>, A> = runConsoleIO<A, B>(trace)(f)
        }

    @Test
    fun `should tell Hello and tell World`() {
        // Given
        val output = mutableListOf<String>()
        val monad = Freer.Over<ConsoleIOK>()

        // When
        runTest {
            val program = with(monad.infix) {
                monad.tell("Hello") bind {
                    monad.tell("World")
                }
            }

            monad.run(runConsole(output), program)
        }

        // Then
        val expected = listOf("Tell Hello", "Tell World")

        assertEquals(expected, output)
    }

    @Test
    fun `should Ask Name and tell Alice`() {
        // Given
        val output = mutableListOf<String>()
        val monad = Freer.Over<ConsoleIOK>()

        // When
        runTest {
            val program = with(monad) {
                `do` {
                    ask("Name").bind()
                    tell("Alice").bind()
                }
            }

            monad.run(runConsole(output), program)
        }

        // Then
        val expected = listOf("Ask Name?", "Tell Alice")

        assertEquals(expected, output)
    }
}