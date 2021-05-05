package io.smallibs.pilin.type

typealias Supplier<B> = suspend () -> B
typealias Fun<A, B> = suspend (A) -> B
typealias Fun2<A, B, C> = suspend (A, B) -> C
