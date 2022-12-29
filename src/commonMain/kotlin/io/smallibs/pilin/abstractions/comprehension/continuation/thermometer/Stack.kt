package io.smallibs.pilin.abstractions.comprehension.continuation.thermometer

import io.smallibs.pilin.core.Standard.id
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.standard.option.Option.OptionK
import io.smallibs.pilin.standard.option.Option.OptionK.fold
import io.smallibs.pilin.type.App

internal data class Stack<A>(private val value: List<A>) {
    constructor() : this(listOf())

    suspend fun push(a: A): Stack<A> = Stack(listOf(a)) + this

    suspend fun pop(): App<OptionK, Pair<A, Stack<A>>> = if (this.value.isEmpty()) {
        Option.none()
    } else {
        Option.some(this.value[0] to Stack(this.value.subList(1, this.value.size)))
    }

    internal suspend fun pop(d: A): Pair<A, Stack<A>> = pop().fold({ d to this }, ::id)

    suspend fun reversed(): Stack<A> = Stack(this.value.reversed())

    // Monoid should be specified here
    operator fun plus(s: Stack<A>): Stack<A> = Stack(this.value + s.value)
}
