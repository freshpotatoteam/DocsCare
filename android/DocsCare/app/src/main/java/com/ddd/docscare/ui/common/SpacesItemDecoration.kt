package com.ddd.docscare.ui.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val space: Int,
                           private val spanCount: Int = 2): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = space

        // gridlayout의 경우 item이 여러개일 수 있음
        val position = parent.getChildLayoutPosition(view)
        if (position == 0 && position == 1 && position == 2) {
            outRect.top = space
        } else {
            outRect.top = 0
        }

        // grid item일 경우 left, right margin 설정
        if(view.layoutParams is GridLayoutManager.LayoutParams) {
            val lp = view.layoutParams as GridLayoutManager.LayoutParams
            val spanIndex = lp.spanIndex
            when(spanCount) {
                2 -> {
                    when (spanIndex) {
                        0 -> {
                            outRect.left = space
                            outRect.right = space / 2
                            println("#2 @0 ${outRect.left} ${outRect.right}")
                        }
                        1 -> {
                            outRect.left = space / 2
                            outRect.right = space
                            println("${outRect.left} ${outRect.right}")
                            println("#2 @1 ${outRect.left} ${outRect.right}")
                        }
                    }
                }
                3 -> {
                    when (spanIndex) {
                        0 -> {
                            outRect.left = space
                            outRect.right = space / 2
                            println("#3 @0 ${outRect.left} ${outRect.right}")
                        }
                        1 -> {
                            outRect.left = space / 2
                            outRect.right = space / 2
                            println("#3 @1 ${outRect.left} ${outRect.right}")
                        }
                        else -> {
                            outRect.left = space / 2
                            outRect.right = space
                            println("#3 @2 ${outRect.left} ${outRect.right}")
                        }
                    }
                }
            }
        }
    }

}