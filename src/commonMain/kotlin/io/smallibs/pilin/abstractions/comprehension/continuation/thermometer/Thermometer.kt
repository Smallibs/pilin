package io.smallibs.pilin.abstractions.comprehension.continuation.thermometer

import io.smallibs.pilin.abstractions.comprehension.continuation.Control
import io.smallibs.pilin.abstractions.comprehension.continuation.thermometer.Frame.Enter
import io.smallibs.pilin.abstractions.comprehension.continuation.thermometer.Frame.Return
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.type.Supplier

internal class Thermometer<A> private constructor(private var context: Context<A>) : Control<A> {

    private class Done(val value: Any) : RuntimeException()

    override suspend fun reset(block: Supplier<A>): A {
        return runWithFuture(block, Stack())
    }

    override suspend fun <B> shift(f: Fun<Fun<B, A>, A>): B {
        val (frame, future) = context.state.future.pop(Enter)

        context = context.setFuture(future)

        when (frame) {
            is Return<*> -> {
                context = context.addToPast(frame)
                return Universal<B>().fromU(frame.value)
            }

            is Enter -> {
                val newFuture = context.state.past
                val block = context.state.block
                val k: Fun<B, A> = { v: B ->
                    runWithFuture(block, newFuture.push(Return(v)).reversed())
                }
                context = context.addToPast(Enter)
                throw Done(f(k) as Any)
            }
        }
    }

    private suspend fun runWithFuture(f: Supplier<A>, future: Stack<Frame>): A = try {
        context = context.switch(f, future)
        f()
    } catch (d: Done) {
        Universal<A>().fromU(d.value)
    } finally {
        context = context.returns()
    }

    companion object {
        fun <A> new(context: Context<A>): Thermometer<A> = Thermometer(context)
    }
}
