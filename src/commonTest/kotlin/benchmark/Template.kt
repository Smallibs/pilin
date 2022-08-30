package benchmark

import io.smallibs.pilin.standard.reader.Reader
import io.smallibs.pilin.standard.reader.Reader.ReaderK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.utils.unsafeSyncRun
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
class Template {

    sealed interface Document {
        data class Const(val value: String) : Document
        data class Var(val name: String) : Document
        data class Seq(val lhd: Document, val rhd: Document) : Document
    }

    suspend fun Map<String, String>.direct(template: Document): String = when (template) {
        is Document.Const -> {
            template.value
        }

        is Document.Var -> {
            this[template.name] ?: "N/A"
        }

        is Document.Seq -> {
            direct(template.lhd) + direct(template.rhd)
        }
    }

    suspend fun <F> Reader.OverMonad<F, Map<String, String>>.withReader(template: Document): App<Reader.ReaderK<F, Map<String, String>>, String> =
        with(this.infix) {
            when (template) {
                is Document.Const -> {
                    returns(template.value)
                }

                is Document.Var -> {
                    ask.map { it[template.name] ?: "N/A" }
                }

                is Document.Seq -> {
                    withReader(template.lhd).bind { lhd ->
                        withReader(template.rhd).map {
                            lhd + it
                        }
                    }
                }
            }
        }

    suspend fun <F> Reader.OverMonad<F, Map<String, String>>.withReaderAndDo(template: Document): App<Reader.ReaderK<F, Map<String, String>>, String> =
        `do` {
            when (template) {
                is Document.Const -> {
                    template.value
                }

                is Document.Var -> {
                    ask.bind()[template.name] ?: "N/A"
                }

                is Document.Seq -> {
                    withReaderAndDo(template.lhd).bind() + withReaderAndDo(template.rhd).bind()
                }
            }
        }

    companion object {
        val template = Document.Seq(Document.Const("Hello, "), Document.Var("world"))
    }

    @Benchmark
    fun direct() {
        return unsafeSyncRun { mapOf("world" to "World!").direct(template) }
    }

    @Benchmark
    fun withReader() {
        return unsafeSyncRun {
            Reader.Over<Map<String, String>>().withReader(template).invoke(mapOf("world" to "World!"))
        }
    }

    @Benchmark
    fun withReaderAndDo() {
        return unsafeSyncRun {
            Reader.Over<Map<String, String>>().withReaderAndDo(template).invoke(mapOf("world" to "World!"))
        }
    }

}
