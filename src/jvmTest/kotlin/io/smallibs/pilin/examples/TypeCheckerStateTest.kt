package io.smallibs.pilin.examples

import io.smallibs.pilin.examples.TypeCheckerStateTest.Expr.EApply
import io.smallibs.pilin.examples.TypeCheckerStateTest.Expr.EVar
import io.smallibs.pilin.examples.TypeCheckerStateTest.Type.TArrow
import io.smallibs.pilin.examples.TypeCheckerStateTest.Type.TLiteral
import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.standard.state.State
import io.smallibs.pilin.standard.state.State.StateK
import io.smallibs.pilin.standard.state.State.StateK.Companion.invoke
import io.smallibs.pilin.type.App
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class TypeCheckerStateTest {

    private sealed interface Literal {
        data class LInt(val value: Int) : Literal
        data class LString(val value: String) : Literal
    }

    private sealed interface Expr {
        data class ELiteral(val value: Literal) : Expr
        data class ELambda(val binding: String, val type: Type, val body: Expr) : Expr
        data class EApply(val abstraction: Expr, val parameter: Expr) : Expr
        data class EVar(val name: String) : Expr
    }

    private sealed interface Type {
        data class TLiteral(val name: String) : Type
        data class TArrow(val lhd: Type, val rhd: Type) : Type
    }

    private suspend fun <F> State.OverMonad<F, Map<String, Type>>.typeCheck(expr: Expr): App<StateK<F, Map<String, Type>>, Type?> =
        `do` {
            when (expr) {
                is EApply -> {
                    when (val abstraction = typeCheck(expr.abstraction).bind()) {
                        is TArrow -> {
                            val parameter = typeCheck(expr.parameter).bind()
                            if (abstraction.lhd == parameter) abstraction.rhd else null
                        }

                        else -> null
                    }
                }

                is Expr.ELambda -> {
                    modify { it + (expr.binding to expr.type) }.bind()
                    typeCheck(expr.body).bind()
                }

                is Expr.ELiteral -> when (expr.value) {
                    is Literal.LInt -> TLiteral("string")
                    is Literal.LString -> TLiteral("int")
                }

                is EVar -> {
                    get().bind()[expr.name]
                }
            }
        }

    @Test
    fun `should type check the expression`() {
        // Given
        val expr = Expr.ELambda("x", TLiteral("int"), EApply(EVar("add"), EVar("x")))
        val gamma = mapOf("add" to TArrow(TLiteral("int"), TArrow(TLiteral("int"), TLiteral("int"))))
        val state = State.Over<Map<String, Type>>()

        // When
        val type = runBlocking { state.typeCheck(expr)(gamma).fold { it.first } }

        // Then
        val expected = TArrow(TLiteral("int"), TLiteral("int"))

        assertEquals(expected, type)
    }

    @Test
    fun `should not type check the expression`() {
        // Given
        val expr = Expr.ELambda("x", TLiteral("int"), EApply(EVar("add"), EVar("x")))
        val state = State.Over<Map<String, Type>>()
        val gamma = mapOf(
            "add" to TArrow(
                TLiteral("string"), TArrow(TLiteral("int"), TLiteral("int"))
            )
        )

        // When
        val type = runBlocking {
            state.typeCheck(expr)(gamma).fold { it.first }
        }

        // Then
        val expected = null

        assertEquals(expected, type)
    }
}