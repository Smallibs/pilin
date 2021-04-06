package io.smallibs.pilin.module

suspend fun <M, B> open(m: M, f: suspend M.() -> B): B = m.f()
