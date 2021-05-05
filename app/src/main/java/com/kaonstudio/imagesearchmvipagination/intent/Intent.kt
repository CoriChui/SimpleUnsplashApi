package com.kaonstudio.imagesearchmvipagination.intent

interface Intent<T> {
    fun reduce(prevState: T): T
}

fun <T> intent(block: T.() -> T) = object : Intent<T> {
    override fun reduce(prevState: T): T = block(prevState)
}

fun <T> sideEffect(block: T.() -> Unit): Intent<T> = object : Intent<T> {
    override fun reduce(prevState: T): T = prevState.apply(block)
}