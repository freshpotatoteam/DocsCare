package com.ddd.docscare.ui.scan

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View.VISIBLE
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.common.SELECTED_BITMAP
import com.ddd.docscare.util.getEdgePoints
import com.ddd.docscare.util.scaledBitmap
import com.scanlibrary.Utils
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : BaseActivity() {

    var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        initToolbar()
        initLayout()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow)

        //TODO 메뉴 추가
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

        // TODO 스캔된 이미지 Crop
        // TODO 하단 버튼
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
