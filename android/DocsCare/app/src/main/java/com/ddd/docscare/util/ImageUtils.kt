package com.ddd.docscare.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.widget.ImageView
import com.scanlibrary.PolygonView
import com.scanlibrary.ScanActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
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


fun getScannedBitmap(original: Bitmap, imageView: ImageView, points: Map<Int, PointF>): Bitmap {
    val width = original.width
    val height = original.height
    val xRatio = original.width.toFloat() / imageView.width
    val yRatio = original.height.toFloat() / imageView.height

    val x1 = points.getValue(0).x * xRatio
    val x2 = points.getValue(1).x * xRatio
    val x3 = points.getValue(2).x * xRatio
    val x4 = points.getValue(3).x * xRatio
    val y1 = points.getValue(0).y * yRatio
    val y2 = points.getValue(1).y * yRatio
    val y3 = points.getValue(2).y * yRatio
    val y4 = points.getValue(3).y * yRatio
    return ScanActivity().getScannedBitmap(original, x1, y1, x2, y2, x3, y3, x4, y4)
}


/**
 * Image Rotate
 */
var degree = 0f
fun bitmapRotate(src: Bitmap): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree + 90)
    return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
}

/**
 * bitmap to file
 */
fun saveBitmapToFile(context: Context, bitmap: Bitmap, name: String): String {

    val storage = context.cacheDir // 임시파일 저장 경로

    val fileName = "$name.png"

    val tempFile = File(storage, fileName)

    try {
        tempFile.createNewFile()
        val out = FileOutputStream(tempFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)  // bitmap png(손실압축)으로 저장해줌

        out.close()

    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return tempFile.absolutePath
}