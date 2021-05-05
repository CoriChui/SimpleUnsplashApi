package com.example.imagesearchmvvmpagination.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpacingItemDecorator(
    private val hSpaceDimension: Int,
    private val vSpaceDimension: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = hSpaceDimension
        outRect.right = hSpaceDimension
        outRect.top = vSpaceDimension
        outRect.bottom = vSpaceDimension
    }
}