package io.smallibs.pilin.control.extension.delimited.thermometer

sealed interface Frame {
    object Enter : Frame
    data class Return<A>(val value: A) : Frame
}
