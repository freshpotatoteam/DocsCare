package com.ddd.docscare.ui

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class SpacesItemDecoration(private val space: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = space

        val position = parent.getChildLayoutPosition(view)
        if (position == 0 && position == 1 && position == 2) {
            outRect.top = space
        } else {
            outRect.top = 0
        }
    }
}