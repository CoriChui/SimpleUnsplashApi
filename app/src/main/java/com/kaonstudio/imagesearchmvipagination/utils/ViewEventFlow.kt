package com.kaonstudio.imagesearchmvipagination.utils

import kotlinx.coroutines.flow.Flow

interface ViewEventFlow<E> {
    fun viewEvents(): Flow<E>
}