package io.smallibs.pilin.type

typealias SupplierCall<A, B> = suspend A.() -> B
typealias MethFun<A, B, C> = suspend A.(B) -> C
typealias MethFun2<A, B, C, D> = suspend A.(B, C) -> D
typealias MethFun3<A, B, C, D, E> = suspend A.(B, C, D) -> E
typealias Supplier<B> = suspend () -> B
typealias Id<A> = suspend (A) -> A
typealias Fun<A, B> = suspend (A) -> B
typealias Fun2<A, B, C> = suspend (A, B) -> C
typealias Fun3<A, B, C, D> = suspend (A, B, C) -> D
