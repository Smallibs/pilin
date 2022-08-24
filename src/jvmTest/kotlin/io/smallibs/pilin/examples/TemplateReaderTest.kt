package io.smallibs.pilin.examples

import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.IdentityK.Companion.fold
import io.smallibs.pilin.standard.reader.Reader
import io.smallibs.pilin.standard.reader.Reader.ReaderK.Companion.invoke
import io.smallibs.pilin.type.App
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class TemplateReaderTest {

    private sealed interface Template {
        data class Const(val value: String) : Template
        data class Var(val name: String) : Template
        data class Seq(val lhd: Template, val rhd: Template) : Template
    }

    private suspend fun Reader.Over<Map<String, String>>.runNow(env: Map<String, String>, template: Template): String {
        suspend fun execute(template: Template): App<Reader.ReaderK<Identity.IdentityK, Map<String, String>>, String> =
            `do` {
                when (template) {
                    is Template.Const -> {
                        template.value
                    }

                    is Template.Var -> {
                        ask.bind()[template.name] ?: "N/A"
                    }

                    is Template.Seq -> {
                        execute(template.lhd).bind() + execute(template.rhd).bind()
                    }
                }
            }

        return execute(template)(env).fold { it }
    }

    @Test
    fun `should transform template to a string`() {
        // Given
        val template = Template.Seq(Template.Const("Hello, "), Template.Var("world"))
        val reader = Reader.Over<Map<String, String>>()

        // When
        val result = runBlocking { reader.runNow(mapOf("world" to "World!"), template) }

        // Then
        val expected = "Hello, World!"

        assertEquals(expected, result)
    }

    @Test
    fun `should partially transform template to a string`() {
        // Given
        val template = Template.Seq(Template.Const("Hello, "), Template.Var("world"))
        val reader = Reader.Over<Map<String, String>>()

        // When
        val result = runBlocking { reader.runNow(mapOf(), template) }

        // Then
        val expected = "Hello, N/A"

        assertEquals(expected, result)
    }
}
