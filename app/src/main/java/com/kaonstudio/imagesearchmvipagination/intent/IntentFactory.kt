package com.kaonstudio.imagesearchmvipagination.intent

interface IntentFactory<T> {
    fun process(viewEvent: T)
}