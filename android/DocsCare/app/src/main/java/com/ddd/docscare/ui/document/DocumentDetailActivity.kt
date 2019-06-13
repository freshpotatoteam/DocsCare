package com.ddd.docscare.ui.document

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.base.BaseRecyclerAdapter
import com.ddd.docscare.model.DocumentDetailItem
import com.ddd.docscare.ui.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_document_detail.*
import kotlinx.android.synthetic.main.activity_document_detail_header_item.view.*
import kotlinx.android.synthetic.main.activity_document_detail_item.view.*
import kotlinx.android.synthetic.main.activity_document_detail_title_item.view.*
import java.io.File
import java.io.IOException
import java.util.*

class DocumentDetailActivity : BaseActivity() {

    private val adapter by lazy { DocumentDetailAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_detail)

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

    private fun initLayout() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(adapter.getItemViewType(position)) {
                    HEADER -> 2
                    TITLE -> 2
                    else -> 1
                }
            }
        }
        documentDetailRecyclerView.layoutManager = gridLayoutManager
        documentDetailRecyclerView.adapter = adapter
        documentDetailRecyclerView.addItemDecoration(
            SpacesItemDecoration(
                resources.getDimensionPixelSize(R.dimen.doc_detail_item_space)
            )
        )
    }

    private fun loadPdfImages() {
        var imagePath = intent.extras.getString("IMAGE_PATH")!!
        imagePath = "/storage/emulated/0/pdffiles/tedt1.pdf"

        val items = ArrayList<DocumentDetailItem>()
        items.add(DocumentDetailItem(type = HEADER, position = 0))
        items.add(DocumentDetailItem(type = TITLE, position = 1, title = "Docs Care_ 2019.06.03"))

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
                    items.add(DocumentDetailItem(bitmap, i+1, type = CONTENT))
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

    class DocumentDetailAdapter(private val base: BaseActivity):
        BaseRecyclerAdapter<DocumentDetailItem, RecyclerView.ViewHolder>() {

        override fun onBindView(
            holder: RecyclerView.ViewHolder,
            item: DocumentDetailItem,
            position: Int
        ) {
            when(holder) {
                is DocDetailHeaderlViewHolder -> {
                    initToolbar(base, holder.itemView.toolbar)
                }

                is DocDetailTitlelViewHolder -> {
                    holder.itemView.documentDetailTitle.text = item.title
                }

                is DocDetailViewHolder -> {
                    val itemView = holder.itemView
                    itemView.image.setImageBitmap(item.bitmap)
                    itemView.imageNum.text = "${item.position}/${getItem().size - 2}"
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
            return when(type) {
                HEADER -> DocDetailHeaderlViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.activity_document_detail_header_item,
                        parent,
                        false
                    )
                )

                TITLE -> DocDetailTitlelViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.activity_document_detail_title_item,
                        parent,
                        false
                    )
                )

                else -> DocDetailViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.activity_document_detail_item,
                        parent,
                        false
                    )
                )
            }

        }

        override fun getItemViewType(position: Int): Int {
            return when(position) {
                0 -> return HEADER
                1 -> return TITLE
                else -> CONTENT
            }
        }

        inner class DocDetailHeaderlViewHolder(view: View): RecyclerView.ViewHolder(view)
        inner class DocDetailTitlelViewHolder(view: View): RecyclerView.ViewHolder(view)
        inner class DocDetailViewHolder(view: View): RecyclerView.ViewHolder(view)
    }


    companion object {

        const val HEADER = 0
        const val TITLE = 1
        const val CONTENT = 2

        fun startActivity(context: Context, imagePath: String) {
            val bundle= Bundle()
            bundle.putString("IMAGE_PATH", imagePath)

            val intent = Intent(context, DocumentDetailActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        fun initToolbar(base: BaseActivity, toolbar: Toolbar) {
            base.setSupportActionBar(toolbar)
            base.supportActionBar?.setDisplayShowTitleEnabled(false)
            base.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            base.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow)

            // TODO menu 추가
        }
    }
}
