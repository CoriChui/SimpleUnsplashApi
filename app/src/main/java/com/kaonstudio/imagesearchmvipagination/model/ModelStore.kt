package com.kaonstudio.imagesearchmvipagination.model

import com.kaonstudio.imagesearchmvipagination.intent.Intent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@ExperimentalCoroutinesApi
@FlowPreview
open class ModelStore<S>(initialState: S) : Model<S> {

    private val scope = MainScope()
    private val intents = Channel<Intent<S>>()
    private val store = ConflatedBroadcastChannel(initialState)

    init {
        scope.launch {
            while (isActive) {
                store.offer(intents.receive().reduce(store.value))
            }
        }
    }

    override fun process(intent: Intent<S>) {
        intents.offer(intent)
    }

    override fun state(): Flow<S> {
        return store.asFlow().distinctUntilChanged()
    }

    override fun indistinctState(): Flow<S> {
        return store.asFlow()
    }

    fun close() {
        intents.close()
        store.close()
        scope.cancel()
    }
}
