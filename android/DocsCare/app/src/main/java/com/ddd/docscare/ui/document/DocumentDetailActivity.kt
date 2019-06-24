package com.ddd.docscare.ui.document

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.base.BaseRecyclerAdapter
import com.ddd.docscare.model.DocumentDetailItem
import com.ddd.docscare.ui.common.SpacesItemDecoration
import com.ddd.docscare.util.AndroidExtensionsViewHolder
import kotlinx.android.synthetic.main.activity_document_detail.*
import kotlinx.android.synthetic.main.activity_document_detail_item.view.*
import java.io.File
import java.io.IOException
import java.util.*

class DocumentDetailActivity : BaseActivity() {

    private val adapter by lazy { DocumentDetailAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_detail)

        initToolbar()
        initLayout()
        loadPdfImages()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_document_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.activity_doc_detail_menu_merge -> {
                Toast.makeText(this@DocumentDetailActivity, "Clicked..", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initLayout() {
        documentDetailRecyclerView.layoutManager = GridLayoutManager(this, 2)
        documentDetailRecyclerView.adapter = adapter
        documentDetailRecyclerView.addItemDecoration(
            SpacesItemDecoration(
                resources.getDimensionPixelSize(R.dimen.doc_detail_item_space)
            )
        )

        documentDetailTitle.text = "Docs Care_ 2019.06.03"
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow)
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
                    items.add(DocumentDetailItem(bitmap, i))
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
            val itemView = holder.itemView
            itemView.image.setImageBitmap(item.bitmap)
            itemView.imageNum.text = "${item.position + 1}/${getItem().size}"
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
            return DocDetailViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.activity_document_detail_item,
                parent,
                false
            ))
        }

        inner class DocDetailViewHolder(view: View): AndroidExtensionsViewHolder(view)
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
