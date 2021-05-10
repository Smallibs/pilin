package io.smallibs.pilin.effect

data class Effects<O, H : Handler>(private val block: suspend Effects<O, H>.(H) -> O) {

    infix fun with(effect: () -> H): HandledEffects<O> =
        with(effect())

    infix fun with(effect: H): HandledEffects<O> =
        HandledEffects { block(effect) }

    companion object {
        fun <O, H : Handler> handle(block: suspend Effects<O, H>.(H) -> O): Effects<O, H> =
            Effects(block)
    }
}
