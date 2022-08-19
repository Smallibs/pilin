package io.smallibs.pilin.abstractions

import io.smallibs.pilin.abstractions.PreCategory.FromMonad.FunK
import io.smallibs.pilin.abstractions.PreCategory.FromMonad.FunK.Companion.lift
import io.smallibs.pilin.type.App2

object Category {

    interface Core<T> : PreCategory.Core<T> {
        suspend fun <A> id(): App2<T, A, A>
    }

    class FromMonad<F>(override val monad: Monad.Core<F>) : API<FunK<F>>, PreCategory.FromMonad<F>(monad) {
        override suspend fun <A> id(): App2<FunK<F>, A, A> {
            return lift { monad.returns(it) }
        }
    }

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    class Operation<T>(private val c: Core<T>) : PreCategory.Operation<T>(c), Core<T> by c

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    class Infix<T>(private val c: Core<T>) : PreCategory.Infix<T>(c), Core<T> by c

    interface API<T> : Core<T> {
        val operation: Operation<T> get() = Operation(this)
        val infix: Infix<T> get() = Infix(this)
    }

}