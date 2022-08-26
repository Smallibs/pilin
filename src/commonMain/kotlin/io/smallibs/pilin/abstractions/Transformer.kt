package io.smallibs.pilin.abstractions

import io.smallibs.pilin.type.App

interface Transformer<F, G> {
    suspend fun <A> transform(ma: App<F, A>): App<G, A>
}