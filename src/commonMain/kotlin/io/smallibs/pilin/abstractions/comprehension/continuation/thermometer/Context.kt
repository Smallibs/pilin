package io.smallibs.pilin.abstractions.comprehension.continuation.thermometer

import io.smallibs.pilin.core.Standard.id
import io.smallibs.pilin.standard.option.Option.OptionK.Companion.fold
import io.smallibs.pilin.type.Supplier

internal class Context<A> private constructor(val state: State<A>, private val nested: Stack<State<A>>) {

    constructor() : this(State(null, Stack(), Stack()), Stack())

    suspend fun setFuture(future: Stack<Frame>) =
        Context(State(this.state.block, this.state.past, future), this.nested)

    suspend fun addToPast(frame: Frame) =
        Context(State(this.state.block, this.state.past.push(frame), this.state.future), this.nested)

    suspend fun switch(f: Supplier<A>, future: Stack<Frame>) =
        Context(State(f, Stack(), future), this.nested.push(this.state))

    suspend fun returns(): Context<A> {
        val (prev, nest) = this.nested.pop().fold({ throw IndexOutOfBoundsException() }, ::id)
        return Context(prev, nest)
    }
}
