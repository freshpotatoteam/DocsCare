package com.ddd.docscare.ui.scan

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.common.SELECTED_BITMAP
import com.ddd.docscare.util.*
import com.scanlibrary.Utils
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : BaseActivity() {

    var image: Bitmap? = null
    var canFull: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        initToolbar()
        initLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_scan_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> { finish() }
            R.id.activity_scan_menu_save -> {

                sourceFrame.post {
                    val points = polygonView.points
                    val scannedBitmap = getScannedBitmap(image!!, sourceImageView, points)
                    sourceImageView.setImageBitmap(scannedBitmap)
                    image?.recycle()
                    polygonView.visibility = GONE
                    //TODO 스캔된 이미지 파일 생성
                    //TODO 스캔된 이미지 서버 전송
                    //
                    //TODO 서버 전송된 결과에서 카테고리 받기
                    //TODO 스캔된 이미지 파일 DB insert
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow)
    }

    private fun initLayout() {
        val uri = intent.extras?.getParcelable<Uri>(SELECTED_BITMAP)

        sourceFrame.post {
            uri?.let {
                image = Utils.getBitmap(this, it)
                contentResolver.delete(it, null, null)
                setBitmap(image)
                drawEdgePoints()
            }
        }

        imageRotate.setOnClickListener {
            image = bitmapRotate(image!!)

            val rotatedBitmap = (sourceImageView.drawable as BitmapDrawable).bitmap
            sourceImageView.setImageBitmap(bitmapRotate(rotatedBitmap))
            drawEdgePoints()
        }

        fullImage.setOnClickListener {
            if(canFull) {
                val tempBitmap = (sourceImageView.drawable as BitmapDrawable).bitmap
                val pointFs = getOutlinePoints(tempBitmap)
                polygonView.points = pointFs
                polygonView.visibility = VISIBLE
                polygonView.invalidate()
            } else {
                drawEdgePoints()
            }

            canFull = !canFull
        }
    }

    private fun setBitmap(bitmap: Bitmap?) {
        if(bitmap == null) return
        val scaledBitmap = scaledBitmap(bitmap, sourceFrame.width, sourceFrame.height)
        sourceImageView.setImageBitmap(scaledBitmap)
    }

    private fun drawEdgePoints() {
        val tempBitmap = (sourceImageView.drawable as BitmapDrawable).bitmap
        val pointFs = getEdgePoints(polygonView, tempBitmap)
        polygonView.points = pointFs
        polygonView.visibility = VISIBLE
        polygonView.invalidate()
    }

    companion object {
        fun startActivity(context: Context, imageUri: Uri) {
            val intent = Intent(context, ScanActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putParcelable(SELECTED_BITMAP, imageUri)
                })
            }

            context.startActivity(intent)
        }
    }
}
