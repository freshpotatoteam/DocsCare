package com.ddd.docscare.ui.document

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.base.BaseRecyclerAdapter
import com.ddd.docscare.ui.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_document_detail.*
import kotlinx.android.synthetic.main.activity_document_detail_item.view.*
import java.io.File
import java.io.IOException

class DocumentDetailActivity : BaseActivity() {

    private val adapter by lazy { DocumentDetailAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_detail)

        initToolbar()
        initLayout()
        loadPdfImages()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow)

        // TODO menu 추가
    }

    private fun initLayout() {
        documentDetailRecyclerView.layoutManager = GridLayoutManager(this, 2)
        documentDetailRecyclerView.adapter = adapter
        documentDetailRecyclerView.addItemDecoration(
            SpacesItemDecoration(
                resources.getDimensionPixelSize(R.dimen.folder_item_space)
            )
        )
    }

    private fun loadPdfImages() {
        var imagePath = intent.extras.getString("IMAGE_PATH")!!
        imagePath = "/storage/emulated/0/pdffiles/tedt1.pdf"

        val items = ArrayList<DocumentDetailItem>()
        try {
            val fileDescriptor = ParcelFileDescriptor.open(File(imagePath), MODE_READ_ONLY)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val renderer = PdfRenderer(fileDescriptor)
                for (i in 0 until renderer.pageCount) {
                    val page = renderer.openPage(i)
                    val bitmap = Bitmap.createBitmap(
                        page.width, page.height,
                        Bitmap.Config.ARGB_8888
                    )

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    items.add(DocumentDetailItem(bitmap, i+1))
                    page.close()
                }
                renderer.close()
            } else {
                TODO("VERSION.SDK_INT < LOLLIPOP")
            }
        } catch (e : IOException) {
            e.printStackTrace()
        } catch (e : OutOfMemoryError) {
            e.printStackTrace()
        } catch (e : SecurityException) {
            e.printStackTrace()
        }

        adapter.addAll(items)
        adapter.notifyDataSetChanged()
    }

    data class DocumentDetailItem(var bitmap: Bitmap, var position: Int)

    class DocumentDetailAdapter(private val context: Context):
        BaseRecyclerAdapter<DocumentDetailItem, RecyclerView.ViewHolder>() {

        override fun onBindView(
            holder: RecyclerView.ViewHolder,
            item: DocumentDetailItem,
            position: Int
        ) {
            val itemView = holder.itemView
            itemView.image.setImageBitmap(item.bitmap)
            itemView.imageNum.text = "${item.position}/${getItem().size}"
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
            return DocumentDetailViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.activity_document_detail_item,
                    parent,
                    false
                )
            )
        }

        inner class DocumentDetailViewHolder(view: View): RecyclerView.ViewHolder(view)
    }


    companion object {

        fun startActivity(context: Context, imagePath: String) {
            val bundle= Bundle()
            bundle.putString("IMAGE_PATH", imagePath)

            val intent = Intent(context, DocumentDetailActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}
