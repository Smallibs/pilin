package io.smallibs.pilin.type

typealias SupplierCall<A, B> = suspend A.() -> B
typealias FunCall<A, B, C> = suspend A.(B) -> C
typealias Fun2Call<A, B, C, D> = suspend A.(B, C) -> D
typealias Fun3Call<A, B, C, D, E> = suspend A.(B, C, D) -> E
typealias Supplier<B> = suspend () -> B
typealias Id<A> = suspend (A) -> A
typealias Fun<A, B> = suspend (A) -> B
typealias Fun2<A, B, C> = suspend (A, B) -> C
typealias Fun3<A, B, C, D> = suspend (A, B, C) -> D
