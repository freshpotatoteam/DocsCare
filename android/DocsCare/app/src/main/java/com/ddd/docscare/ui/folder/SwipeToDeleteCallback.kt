package com.ddd.docscare.ui.folder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.ddd.docscare.R

abstract class SwipeToDeleteCallback(context: Context): ItemTouchHelper.Callback() {

    private val backgroundColor = Color.parseColor("#ff4b52")
    private val background: ColorDrawable = ColorDrawable()
    private val deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_trash)
    private val intrinsicWidth = deleteDrawable?.intrinsicWidth ?: 0
    private val intrinsicHeight = deleteDrawable?.intrinsicHeight ?: 0

    // swipe 방향 설정
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    // drag & drop 에 사용되므로 false
    override fun onMove(
        p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height

        val isCancelled = dX == 0f && !isCurrentlyActive
        if(isCancelled) {
            // clear canvas
            println("isCancelled...")
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        println("--------------------------------------------")
        println("left ${itemView.right + dX.toInt()}   dX : ${dX.toInt()}")
        println("right ${itemView.right} ")
        println("top ${itemView.top} ")
        println("bottom ${itemView.bottom} ")
        println("--------------------------------------------")

        background.color = backgroundColor
        background.setBounds(itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom )
        background.draw(c)

        val iconMargin = (itemHeight - intrinsicHeight) / 2
        val iconTop = itemView.top + iconMargin
        val iconLeft = itemView.right - iconMargin - intrinsicWidth
        val iconRight = itemView.right - iconMargin
        val iconBottom = iconTop + intrinsicHeight


        println("--------------------------------------------")
        println("itemHeight ${(itemHeight)}   intrinsicHeight : $intrinsicHeight")
        println("iconMargin ${(itemHeight - intrinsicHeight) / 2}")
        println("iconTop ${itemView.top + iconMargin}")
        println("iconLeft ${itemView.right - iconMargin - intrinsicWidth} ")
        println("iconRight ${itemView.right - iconMargin} ")
        println("iconBottom ${itemView.bottom + intrinsicHeight} ")
        println("--------------------------------------------")


        deleteDrawable?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        deleteDrawable?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.5f
    }
}