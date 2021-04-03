package io.smallibs.kategory.type

object Fun {
    interface T<in A, out B> {
        suspend operator fun invoke(a: A): B
    }

    private data class Lambda<in A, out B>(val f: (A) -> B) : T<A, B> {
        override suspend fun invoke(a: A): B = f(a)
    }

    private data class SLambda<in A, out B>(val f: suspend (A) -> B) : T<A, B> {
        override suspend fun invoke(a: A): B = f(a)
    }

    object Function {

        fun <A, B> ((A) -> B).lambda(): T<A, B> =
            Lambda(this)

        fun <A, B, C> ((A, B) -> C).lambda(): T<A, T<B, C>> =
            Lambda { a -> Lambda { b -> this(a, b) } }

        fun <A, B, C, D> (A.(B, C) -> D).lambda(): T<A, T<B, T<C, D>>> =
            Lambda { a -> Lambda { b -> Lambda { c -> this(a, b, c) } } }

    }

    object Method {

        fun <A, B> (A.() -> B).lambda(): T<A, B> = Lambda(this)

        fun <A, B, C> (A.(B) -> C).lambda(): T<A, T<B, C>> =
            Lambda { a -> Lambda { b -> this(a, b) } }

        fun <A, B, C, D> (A.(B, C) -> D).lambda(): T<A, T<B, T<C, D>>> =
            Lambda { a -> Lambda { b -> Lambda { c -> this(a, b, c) } } }

    }

    object SFunction {

        fun <A, B> (suspend (A) -> B).lambda(): T<A, B> =
            SLambda(this)

        fun <A, B, C> (suspend (A, B) -> C).lambda(): T<A, T<B, C>> =
            SLambda { a -> SLambda { b -> this(a, b) } }

        fun <A, B, C, D> (suspend (A, B, C) -> D).lambda(): T<A, T<B, T<C, D>>> =
            SLambda { a -> SLambda { b -> SLambda { c -> this(a, b, c) } } }

    }

    object SMethod {

        fun <A, B> (suspend A.() -> B).lambda(): T<A, B> =
            SLambda(this)

        fun <A, B, C> (suspend A.(B) -> C).lambda(): T<A, T<B, C>> =
            SLambda { a -> SLambda { b -> this(a, b) } }

        fun <A, B, C, D> (suspend A.(B, C) -> D).lambda(): T<A, T<B, T<C, D>>> =
            SLambda { a -> SLambda { b -> SLambda { c -> this(a, b, c) } } }

    }
}
