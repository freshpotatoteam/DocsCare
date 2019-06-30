package com.ddd.docscare.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.ddd.docscare.R
import com.ddd.docscare.common.IMAGE_FOLDER_PATH
import com.ddd.docscare.common.PROVIDER
import com.ddd.docscare.common.START_CAMERA_REQUEST_CODE
import com.ddd.docscare.ui.main.Main
import com.ddd.docscare.ui.scan.ScanActivity
import com.scanlibrary.Utils
import kotlinx.android.synthetic.main.bottom_navigation.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 오버라이드할 메서드 정리
 * https://samse.tistory.com/entry/Custom-View-만들기
 */
class BottomNavigationView @JvmOverloads constructor(context: Context,
                           attrs: AttributeSet? = null,
                           defStyleAttr: Int = 0): LinearLayout(context, attrs, defStyleAttr),
    ActivityResultObserver {

    private var fileUri: Uri? = null
    private val sdf by lazy { SimpleDateFormat("yyyyMMdd_HHmmss") }

    init {
        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println("###BottomNavigationView onActivityResult")
        var bitmap: Bitmap? = null

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                START_CAMERA_REQUEST_CODE -> {
                    bitmap = fileUri?.let { getScaledBitmap(it) }
                }
            }

            if(bitmap != null) {
                val uri = Utils.getUri(context, bitmap)
                bitmap.recycle()

                ScanActivity.startActivity(context, uri)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (context as ActivityResultObservable).addObserver(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (context as ActivityResultObservable).removeObserver(this)
    }

    private fun initView() {
        val bottomView = LayoutInflater.from(context)
            .inflate(R.layout.bottom_navigation, this, false)
        addView(bottomView)

        camera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val file = createImageFile()
            file.parentFile.mkdirs()
            val tempFileUri = FileProvider.getUriForFile(context,
                PROVIDER,
                file)

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri)
            (context as AppCompatActivity).startActivityForResult(cameraIntent, START_CAMERA_REQUEST_CODE)
        }

        home.setOnClickListener {
            context.startActivity(Intent(context, Main::class.java))
            (context as AppCompatActivity).finish()
        }

        newFile.setOnClickListener {
            // TODO Load image or pdf
            Toast.makeText(context, "newFile button clicked.. ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        clearTempImages()
        val timeStamp = sdf.format(Date())
        val file = File(IMAGE_FOLDER_PATH, "IMG_$timeStamp.jpg")
        fileUri = Uri.fromFile(file)
        return file
    }

    // camera
    private fun clearTempImages() {
        val tempFolder = File(IMAGE_FOLDER_PATH)
        try {
            for (file in tempFolder.listFiles()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // camera
    private fun getScaledBitmap(selectedImg: Uri): Bitmap {
        val options = BitmapFactory.Options().apply {
            inSampleSize = 3
        }

        val fileDescriptor: AssetFileDescriptor? =
            context.contentResolver.openAssetFileDescriptor(selectedImg, "r")

        return BitmapFactory.decodeFileDescriptor(
            fileDescriptor?.fileDescriptor, null, options)
    }
}