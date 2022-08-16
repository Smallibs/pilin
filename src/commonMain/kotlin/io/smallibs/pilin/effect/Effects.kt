package io.smallibs.pilin.effect

data class Effects<H : EffectHandler, O>(private val block: suspend Effects<H, O>.(H) -> O) {

    infix fun with(effect: () -> H): HandledEffects<O> = with(effect())

    infix fun with(effect: H): HandledEffects<O> = HandledEffects { block(effect) }

    companion object {
        fun <H : EffectHandler, O> handle(block: suspend Effects<H, O>.(H) -> O): Effects<H, O> = Effects(block)
    }
}
