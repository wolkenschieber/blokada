package org.obsolete

typealias KContext = nl.komponents.kovenant.Context

interface IPersistence<T> {
    fun read(current: T): T
    fun write(source: T)
}

