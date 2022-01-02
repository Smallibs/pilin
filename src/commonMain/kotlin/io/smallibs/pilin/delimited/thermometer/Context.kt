package io.smallibs.pilin.delimited.thermometer

import io.smallibs.pilin.type.Supplier

internal class Context<A> private constructor(val state: State<A>, private val nested: Stack<State<A>>) {
    constructor() : this(State(null, Stack(), Stack()), Stack())

    fun setFuture(future: Stack<Frame>) =
        Context(State(this.state.block, this.state.past, future), this.nested)

    fun addToPast(frame: Frame) =
        Context(State(this.state.block, this.state.past + Stack(frame), this.state.future), this.nested)

    fun switch(f: Supplier<A>, future: Stack<Frame>) =
        Context(State(f, Stack(), future), this.nested + Stack(this.state))

    fun returns(): Context<A> {
        val (prev, nest) = this.nested.pop()
        return Context(prev, nest)
    }
}
