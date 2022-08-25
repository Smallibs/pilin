package io.smallibs.pilin.examples

import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.standard.state.State
import io.smallibs.pilin.standard.state.State.StateK
import io.smallibs.pilin.standard.state.State.StateK.Companion.invoke
import io.smallibs.pilin.type.App
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class DeBruijnTermStateTest {

    private sealed interface Term {
        data class Var(val name: String) : Term
        data class App(val abstraction: Term, val parameter: Term) : Term
        data class Abs(val bind: String, val body: Term) : Term
    }

    private sealed interface DBTerm {
        data class Ident(val name: String) : DBTerm
        data class Var(val index: Int) : DBTerm
        data class App(val abstraction: DBTerm, val parameter: DBTerm) : DBTerm
        data class Abs(val body: DBTerm) : DBTerm
    }

    private suspend fun <F> State.OverMonad<F, List<String>>.execute(term: Term): App<StateK<F, List<String>>, DBTerm> =
        `do` {
            when (term) {
                is Term.Abs -> {
                    modify { it + listOf(term.bind) }.bind()
                    DBTerm.Abs(execute(term.body).bind())
                }

                is Term.App -> {
                    val ldh = execute(term.abstraction).bind()
                    val rhd = execute(term.parameter).bind()
                    DBTerm.App(ldh, rhd)
                }

                is Term.Var -> {
                    val index = get().bind().indexOf(term.name)
                    if (index > -1) {
                        DBTerm.Var(index)
                    } else {
                        DBTerm.Ident(term.name)
                    }
                }
            }
        }

    @Test
    fun `should transform a lambda term to a DeBruijn lambda term`() {
        // Given
        val term = Term.App(Term.Abs("x", Term.Var("x")), Term.Var("y"))
        val state = State.Over<List<String>>()

        // When
        val result = runBlocking { state.execute(term)(listOf()).fold { it.first } }

        // Then
        val expected = DBTerm.App(DBTerm.Abs(DBTerm.Var(0)), DBTerm.Ident("y"))

        assertEquals(expected, result)
    }

}
