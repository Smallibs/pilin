package io.smallibs.pilin.effect

data class And<L : EffectHandler, R : EffectHandler>(val left: L, val right: R) : EffectHandler {
    companion object {
        infix fun <L : EffectHandler, R : EffectHandler> L.and(right: R) = And(this, right)
    }
}
