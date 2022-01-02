package io.smallibs.pilin.control.extension.delimited.thermometer

import io.smallibs.pilin.control.extension.delimited.Control
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.fix
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.type.Supplier

internal class Thermometer<A> private constructor(var context: Context<A>) : Control<A> {

    private class Done(val value: Any?) : RuntimeException()

    override suspend fun reset(block: Supplier<A>): A {
        return run(block, Stack())
    }

    override suspend fun <B> shift(f: App<ContinuationK<A>, B>): B {
        val (frame, future) = context.state.future.pop(Frame.Enter)

        context = context.setFuture(future)

        when (frame) {
            is Frame.Return<*> -> {
                context = context.addToPast(frame)
                return Universal.from(frame.value)
            }
            is Frame.Enter -> {
                val newFuture = context.state.past.reversed()
                val block = context.state.block
                val k: Fun<B, A> = { v: B ->
                    run(block!!, newFuture.push(Frame.Return(v)))
                }
                context = context.addToPast(Frame.Enter)
                throw Done(f.fix(k))
            }
        }
    }

    private suspend fun run(f: Supplier<A>, future: Stack<Frame>): A =
        try {
            context = context.switch(f, future)
            f()
        } catch (d: Done) {
            Universal.from(d.value)
        } finally {
            context = context.returns()
        }

    companion object {
        fun <A> run(block: Thermometer<A>.() -> A) =
            Thermometer<A>(Context()).run(block)

        fun <A> new(context: Context<A>): Thermometer<A> = Thermometer(context)
    }
}
