package benchmarks

import io.smallibs.pilin.standard.reader.Reader
import io.smallibs.pilin.standard.reader.Reader.ReaderK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.utils.runTest
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Mode
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
class TemplateReader {

    private sealed interface Template {
        data class Const(val value: String) : Template
        data class Var(val name: String) : Template
        data class Seq(val lhd: Template, val rhd: Template) : Template
    }

    private suspend fun Map<String, String>.direct(template: Template): String = when (template) {
        is Template.Const -> {
            template.value
        }

        is Template.Var -> {
            this[template.name] ?: "N/A"
        }

        is Template.Seq -> {
            direct(template.lhd) + direct(template.rhd)
        }
    }

    private suspend fun <F> Reader.OverMonad<F, Map<String, String>>.withReader(template: Template): App<Reader.ReaderK<F, Map<String, String>>, String> =
        with(this.infix) {
            when (template) {
                is Template.Const -> {
                    returns(template.value)
                }

                is Template.Var -> {
                    ask.map { it[template.name] ?: "N/A" }
                }

                is Template.Seq -> {
                    withReader(template.lhd).bind { lhd ->
                        withReader(template.rhd).map {
                            lhd + it
                        }
                    }
                }
            }
        }

    private suspend fun <F> Reader.OverMonad<F, Map<String, String>>.withReaderAndDo(template: Template): App<Reader.ReaderK<F, Map<String, String>>, String> =
        `do` {
            when (template) {
                is Template.Const -> {
                    template.value
                }

                is Template.Var -> {
                    ask.bind()[template.name] ?: "N/A"
                }

                is Template.Seq -> {
                    withReaderAndDo(template.lhd).bind() + withReaderAndDo(template.rhd).bind()
                }
            }
        }

    @Benchmark
    fun direct() {
        val template = Template.Seq(Template.Const("Hello, "), Template.Var("world"))

        return runTest { mapOf("world" to "World!").direct(template) }
    }

    @Benchmark
    fun withReader() {
        val template = Template.Seq(Template.Const("Hello, "), Template.Var("world"))
        val reader = Reader.Over<Map<String, String>>()

        return runTest { reader.withReader(template).invoke(mapOf("world" to "World!")) }
    }

    @Benchmark
    fun withReaderAndDo() {
        val template = Template.Seq(Template.Const("Hello, "), Template.Var("world"))
        val reader = Reader.Over<Map<String, String>>()

        return runTest { reader.withReaderAndDo(template).invoke(mapOf("world" to "World!")) }
    }
}
