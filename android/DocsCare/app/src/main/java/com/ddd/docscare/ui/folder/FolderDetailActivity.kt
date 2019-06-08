package com.ddd.docscare.ui.folder

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.base.BaseRecyclerAdapter
import com.ddd.docscare.ui.SpacesItemDecoration
import com.ddd.docscare.ui.document.DocumentDetailActivity
import kotlinx.android.synthetic.main.activity_folder_detail.*
import kotlinx.android.synthetic.main.activity_folder_detail_item.view.*

class FolderDetailActivity : BaseActivity() {

    private val adapter by lazy { FolderDetailAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_detail)

        initToolbar()
        initLayout()

        // TODO 아이템 클릭시 아이템 상세보기 화면으로 이동
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
    }

    private fun initLayout() {
        folderDetailTitle.text = "커리어"

        folderDetailRecyclerView.layoutManager = LinearLayoutManager(this)
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
    }

    data class FolderDetailItem(var title: String = "", var path: String = "")
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
