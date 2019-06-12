package com.ddd.docscare.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import com.scanlibrary.PolygonView
import com.scanlibrary.ScanActivity
import java.util.*

/**
 * width, height 크기만큼 크기 조절
 */
fun scaledBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
    val m = Matrix()
    m.setRectToRect(
        RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat()),
        RectF(0f, 0f, width.toFloat(), height.toFloat()),
        Matrix.ScaleToFit.CENTER )

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
}

/**
 * 이미지 엣지 추출
 */
fun getEdgePoints(polygonView: PolygonView, tempBitmap: Bitmap): Map<Int, PointF> {
    val pointFs = getContourEdgePoints(tempBitmap)
    return orderedValidEdgePoints(polygonView, tempBitmap, pointFs)
}

fun getContourEdgePoints(tempBitmap: Bitmap): List<PointF> {
    val points = ScanActivity().getPoints(tempBitmap)
    val x1 = points[0]
    val x2 = points[1]
    val x3 = points[2]
    val x4 = points[3]

    val y1 = points[4]
    val y2 = points[5]
    val y3 = points[6]
    val y4 = points[7]

    val pointFs = ArrayList<PointF>()
    pointFs.add(PointF(x1, y1))
    pointFs.add(PointF(x2, y2))
    pointFs.add(PointF(x3, y3))
    pointFs.add(PointF(x4, y4))
    return pointFs
}

fun orderedValidEdgePoints(polygonView: PolygonView, tempBitmap: Bitmap, pointFs: List<PointF>): Map<Int, PointF> {
    var orderedPoints = polygonView.getOrderedPoints(pointFs)
    if (!polygonView.isValidShape(orderedPoints)) {
        orderedPoints = getOutlinePoints(tempBitmap)
    }
    return orderedPoints
}

fun getOutlinePoints(tempBitmap: Bitmap): Map<Int, PointF> {
    val outlinePoints = HashMap<Int, PointF>()
    outlinePoints[0] = PointF(0f, 0f)
    outlinePoints[1] = PointF(tempBitmap.width.toFloat(), 0f)
    outlinePoints[2] = PointF(0f, tempBitmap.height.toFloat())
    outlinePoints[3] = PointF(tempBitmap.width.toFloat(), tempBitmap.height.toFloat())
    return outlinePoints
}