package io.smallibs.pilin.delimited.thermometer

sealed interface Frame {
    object Enter : Frame
    data class Return<A>(val value: A) : Frame
}
