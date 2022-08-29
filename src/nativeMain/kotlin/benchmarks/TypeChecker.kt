package benchmarks

import benchmarks.TypeChecker.Type.Companion.integer
import benchmarks.TypeChecker.Type.Companion.string
import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.standard.state.State
import io.smallibs.pilin.standard.state.State.StateK
import io.smallibs.pilin.standard.state.State.StateK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.utils.runTest
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Mode
import kotlinx.benchmark.Scope

@kotlinx.benchmark.State(Scope.Benchmark)
class TypeChecker {

    sealed interface Literal {
        data class LInt(val value: Int) : Literal
        data class LString(val value: String) : Literal
    }

    sealed interface Expr {
        data class ELiteral(val value: Literal) : Expr
        data class ELambda(val binding: String, val type: Type, val body: Expr) : Expr
        data class EApply(val abstraction: Expr, val parameter: Expr) : Expr
        data class EVar(val name: String) : Expr
    }

    sealed interface Type {
        data class TLiteral(val name: String) : Type
        data class TArrow(val lhd: Type, val rhd: Type) : Type


        companion object {
            val integer get() = TLiteral("int")
            val string get() = TLiteral("string")
        }
    }

    private suspend fun Map<String, Type>.direct(expr: Expr): Type? = when (expr) {
        is Expr.EApply -> {
            when (val abstraction = direct(expr.abstraction)) {
                is Type.TArrow -> {
                    val parameter = direct(expr.parameter)
                    if (abstraction.lhd == parameter) abstraction.rhd else null

                }

                else -> null
            }
        }


        is Expr.ELambda -> {
            (this + (expr.binding to expr.type)).direct(expr.body)
        }

        is Expr.ELiteral -> when (expr.value) {
            is Literal.LInt -> integer
            is Literal.LString -> string
        }

        is Expr.EVar -> {
            this[expr.name]
        }
    }


    private suspend fun <F> State.OverMonad<F, Map<String, Type>>.withState(expr: Expr): App<StateK<F, Map<String, Type>>, Type?> =
        with(this.infix) {
            when (expr) {
                is Expr.EApply -> {
                    withState(expr.abstraction) bind { abstraction ->
                        when (abstraction) {
                            is Type.TArrow -> {
                                withState(expr.parameter).map { parameter ->
                                    if (abstraction.lhd == parameter) abstraction.rhd else null
                                }
                            }

                            else -> returns(null)
                        }
                    }
                }

                is Expr.ELambda -> {
                    modify { it + (expr.binding to expr.type) }.bind {
                        withState(expr.body)
                    }
                }

                is Expr.ELiteral -> when (expr.value) {
                    is Literal.LInt -> returns(integer)
                    is Literal.LString -> returns(string)
                }

                is Expr.EVar -> {
                    get().map { it[expr.name] }
                }
            }
        }

    private suspend fun <F> State.OverMonad<F, Map<String, Type>>.typeCheckWithStateAndComprehension(expr: Expr): App<StateK<F, Map<String, Type>>, Type?> =
        `do` {
            when (expr) {
                is Expr.EApply -> {
                    when (val abstraction = withState(expr.abstraction).bind()) {
                        is Type.TArrow -> {
                            val parameter = withState(expr.parameter).bind()
                            if (abstraction.lhd == parameter) abstraction.rhd else null
                        }

                        else -> null
                    }
                }

                is Expr.ELambda -> {
                    modify { it + (expr.binding to expr.type) }.bind()
                    withState(expr.body).bind()
                }

                is Expr.ELiteral -> when (expr.value) {
                    is Literal.LInt -> integer
                    is Literal.LString -> string
                }

                is Expr.EVar -> {
                    get().bind()[expr.name]
                }
            }
        }

    companion object {
        val gamma = mapOf("add" to Type.TArrow(integer, Type.TArrow(integer, integer)))
        val expr = Expr.ELambda("x", integer, Expr.EApply(Expr.EVar("add"), Expr.EVar("x")))
    }

    @Benchmark
    fun direct() {
        return runTest { mapOf<String, Type>().direct(expr) }
    }

    @Benchmark
    fun withState() {
        val state = State.Over<Map<String, Type>>()

        return runTest { state.withState(expr)(gamma).fold { it.first } }
    }

    @Benchmark
    fun withStateAndDo() {
        val state = State.Over<Map<String, Type>>()

        return runTest { state.typeCheckWithStateAndComprehension(expr)(gamma).fold { it.first } }
    }
}