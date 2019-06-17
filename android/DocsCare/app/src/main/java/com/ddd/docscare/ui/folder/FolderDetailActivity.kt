package com.ddd.docscare.ui.folder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.base.BaseRecyclerAdapter
import com.ddd.docscare.common.IMAGE_FOLDER_PATH
import com.ddd.docscare.common.PROVIDER
import com.ddd.docscare.common.START_CAMERA_REQUEST_CODE
import com.ddd.docscare.model.FolderDetailItem
import com.ddd.docscare.ui.SpacesItemDecoration
import com.ddd.docscare.ui.document.DocumentDetailActivity
import com.ddd.docscare.ui.scan.ScanActivity
import com.scanlibrary.Utils
import kotlinx.android.synthetic.main.activity_folder_detail.*
import kotlinx.android.synthetic.main.activity_folder_detail_item.view.*
import kotlinx.android.synthetic.main.bottom_navigation.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FolderDetailActivity : BaseActivity() {

    private val adapter by lazy { FolderDetailAdapter(this) }
    private var fileUri: Uri? = null
    private val sdf by lazy { SimpleDateFormat("yyyyMMdd_HHmmss") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_detail)

        initToolbar()
        initLayout()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var bitmap: Bitmap? = null

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                START_CAMERA_REQUEST_CODE -> {
                    bitmap = fileUri?.let { getScaledBitmap(it) }
                }
            }

            if(bitmap != null) {
                val uri = Utils.getUri(this, bitmap)
                bitmap.recycle()

                ScanActivity.startActivity(this, uri)
            }
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow)
    }

    private fun initLayout() {
        folderDetailTitle.text = "커리어"

        folderDetailRecyclerView.layoutManager =
            LinearLayoutManager(this)
        folderDetailRecyclerView.adapter = adapter
        folderDetailRecyclerView.addItemDecoration(
            SpacesItemDecoration(
                resources.getDimensionPixelSize(R.dimen.folder_item_space)
            )
        )

        val swipeToDeleteCallback = object: SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                Toast.makeText(this@FolderDetailActivity,
                    "$i 번째 ${viewHolder.itemView.tv_folder_detail_title.text}삭제 완료",
                    Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(folderDetailRecyclerView)

        // TODO 폴더안에 항목 리스트 상세보기 (bundle : 폴더경로/폴더이름/폴더고유값)
        for(i in 0..30) {
            adapter.add(FolderDetailItem("test$i"))
        }
        adapter.notifyDataSetChanged()

        camera.setOnClickListener { openCamera() }
    }


    // camera
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val file = createImageFile()
        file.parentFile.mkdirs()
        val tempFileUri = FileProvider.getUriForFile(this,
            PROVIDER,
            file)

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri)
        startActivityForResult(cameraIntent, START_CAMERA_REQUEST_CODE)
    }

    // camera
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
            contentResolver.openAssetFileDescriptor(selectedImg, "r")

        return BitmapFactory.decodeFileDescriptor(
            fileDescriptor?.fileDescriptor, null, options)
    }

    class FolderDetailAdapter(private val context: Context): BaseRecyclerAdapter<FolderDetailItem, RecyclerView.ViewHolder>() {
        override fun onBindView(
            holder: RecyclerView.ViewHolder,
            item: FolderDetailItem,
            position: Int
        ) {
            holder.itemView.tv_folder_detail_title.text = item.title
            holder.itemView.setOnClickListener {
                //TODO 이미지 상세보기

                DocumentDetailActivity.startActivity(context, item.path)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
            return FolderDetailViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.activity_folder_detail_item,
                    parent,
                    false
                )
            )
        }

        inner class FolderDetailViewHolder(view: View): RecyclerView.ViewHolder(view)
    }
}
