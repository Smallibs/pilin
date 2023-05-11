package io.smallibs.pilin.abstractions.comprehension.continuation.thermometer

internal sealed interface Frame {
    object Enter : Frame
    data class Return<A>(val value: A) : Frame
}
