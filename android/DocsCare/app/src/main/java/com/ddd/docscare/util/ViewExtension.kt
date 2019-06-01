package com.ddd.docscare.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.MotionEvent
import android.widget.EditText

fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if(v is EditText) {
            if(event.x >= v.width - v.totalPaddingRight) {
                if(event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}

fun Bitmap.blurBitmap(context: Context, blurRadius: Float): Bitmap {
    val outBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val rs = RenderScript.create(context)

    val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

    val allIn = Allocation.createFromBitmap(rs, this)
    val allOut = Allocation.createFromBitmap(rs, outBitmap)

    blurScript.setRadius(blurRadius)
    blurScript.setInput(allIn)
    blurScript.forEach(allOut)

    allOut.copyTo(outBitmap)
    this.recycle()

    rs.destroy()

    return outBitmap
}

