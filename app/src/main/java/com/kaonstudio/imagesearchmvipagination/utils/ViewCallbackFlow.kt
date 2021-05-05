package com.kaonstudio.imagesearchmvipagination.utils

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.SearchView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
fun View.clicks(): Flow<Unit> = callbackFlow {
    val listener = View.OnClickListener { offer(Unit) }
    setOnClickListener(listener)
    awaitClose {
        setOnClickListener(null)
    }
}