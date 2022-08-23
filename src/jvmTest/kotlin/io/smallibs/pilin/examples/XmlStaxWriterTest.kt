package io.smallibs.pilin.examples

import io.smallibs.pilin.standard.identity.Identity.IdentityK
import io.smallibs.pilin.standard.identity.Identity.IdentityK.Companion.fold
import io.smallibs.pilin.standard.list.List
import io.smallibs.pilin.standard.writer.Writer
import io.smallibs.pilin.standard.writer.Writer.WriterK
import io.smallibs.pilin.standard.writer.Writer.WriterK.Companion.run
import io.smallibs.pilin.type.App
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class XmlStaxWriterTest {

    private sealed interface Xml {
        data class Tag(val name: String, val content: Xml) : Xml
        data class Text(val text: String) : Xml
        data class Seq(val lhd: Xml, val rhd: Xml) : Xml
        object Empty : Xml
    }

    private sealed interface Stax {
        data class Open(val name: String) : Stax
        data class Text(val text: String) : Stax
        data class Close(val name: String) : Stax
    }

    private suspend fun Writer.OverMonoid<List<Stax>>.runNow(xml: Xml): List<Stax> {
        suspend fun execute(xml: Xml): App<WriterK<IdentityK, List<Stax>>, Unit> = `do` {
            when (xml) {
                Xml.Empty -> {
                    // Nothing
                }

                is Xml.Seq -> {
                    execute(xml.lhd).bind()
                    execute(xml.rhd).bind()
                }

                is Xml.Tag -> {
                    tell(List(listOf(Stax.Open(xml.name)))).bind()
                    execute(xml.content).bind()
                    tell(List(listOf(Stax.Close(xml.name)))).bind()
                }

                is Xml.Text -> {
                    tell(List(listOf(Stax.Text(xml.text)))).bind()
                }
            }
        }

        return execute(xml).run.fold { it.second }
    }

    @Test
    fun `should emit stax events while traversing an xml term`() {
        // Given
        val xml = Xml.Tag("A", Xml.Seq(Xml.Text("B"), Xml.Tag("C", Xml.Empty)))
        val monad = Writer.OverMonoid<List<Stax>>(List.monoid())

        // When
        val result = runBlocking { monad.runNow(xml) }

        // Then
        val expected =
            List(Stax.Open("A"), Stax.Text("B"), Stax.Open("C"), Stax.Close("C"), Stax.Close("A"))

        assertEquals(expected, result)
    }
}
