package io.smallibs.pilin.effect

import io.smallibs.pilin.type.Supplier

class HandledEffects<O>(private val result: Supplier<O>) {
    suspend operator fun invoke() = result()
}
