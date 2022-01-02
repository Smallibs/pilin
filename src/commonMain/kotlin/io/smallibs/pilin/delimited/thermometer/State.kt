package io.smallibs.pilin.delimited.thermometer

import io.smallibs.pilin.type.Supplier

internal data class State<A>(
    val block: Supplier<A>?,
    val past: Stack<Frame>,
    val future: Stack<Frame>,
)
