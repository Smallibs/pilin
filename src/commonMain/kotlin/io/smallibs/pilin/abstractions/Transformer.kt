package io.smallibs.pilin.abstractions

import io.smallibs.pilin.type.App

interface Transformer<F, G> {
    suspend operator fun <A> invoke(ma: App<F, A>): App<G, A>
}