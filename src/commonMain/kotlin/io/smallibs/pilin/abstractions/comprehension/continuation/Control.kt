package io.smallibs.pilin.abstractions.comprehension.continuation

import io.smallibs.pilin.abstractions.comprehension.continuation.thermometer.Context
import io.smallibs.pilin.abstractions.comprehension.continuation.thermometer.Thermometer
import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.type.Supplier

interface Control<A> {
    suspend fun reset(block: Supplier<A>): A
    suspend fun <B> shift(f: Continuation<B, A>): B

    companion object {
        fun <A> new(): Control<A> = Thermometer.new(Context())
    }
}
