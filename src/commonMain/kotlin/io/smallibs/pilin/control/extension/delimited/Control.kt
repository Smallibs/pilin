package io.smallibs.pilin.control.extension.delimited

import io.smallibs.pilin.control.extension.delimited.thermometer.Context
import io.smallibs.pilin.control.extension.delimited.thermometer.Thermometer
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Supplier

interface Control<A> {
    suspend fun reset(block: Supplier<A>): A
    suspend fun <B> shift(f: App<ContinuationK<A>, B>): B

    companion object {
        fun <A> new(): Control<A> = Thermometer.new(Context())
    }
}
