package com.kaonstudio.imagesearchmvipagination.model

import com.kaonstudio.imagesearchmvipagination.intent.Intent
import kotlinx.coroutines.flow.Flow

interface Model<R> {

    fun process(intent: Intent<R>)

    fun state(): Flow<R>

    fun indistinctState() : Flow<R>

}