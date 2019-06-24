package com.ddd.docscare.ui.folder

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseRecyclerAdapter
import com.ddd.docscare.base.BaseViewModel
import com.ddd.docscare.db.dto.RecentlyViewedItemDTO
import com.ddd.docscare.model.FolderDetailItem
import com.ddd.docscare.ui.common.ActivityResultObservableActivity
import com.ddd.docscare.ui.common.SpacesItemDecoration
import com.ddd.docscare.ui.document.DocumentDetailActivity
import com.ddd.docscare.ui.main.RecentlyItemViewModel
import com.ddd.docscare.util.AndroidExtensionsViewHolder
import kotlinx.android.synthetic.main.activity_folder_detail.*
import kotlinx.android.synthetic.main.activity_folder_detail_item.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FolderDetailActivity : ActivityResultObservableActivity() {

    private val viewModel: RecentlyItemViewModel by viewModel()
    private val adapter by lazy { FolderDetailAdapter(this, viewModel) }

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
            adapter.add(FolderDetailItem("DocsCare_${i}_2019.06.03"))
        }
        adapter.notifyDataSetChanged()
    }

    class FolderDetailAdapter(private val context: Context,
                              private val viewModel: BaseViewModel):
        BaseRecyclerAdapter<FolderDetailItem, RecyclerView.ViewHolder>() {

        override fun onBindView(
            holder: RecyclerView.ViewHolder,
            item: FolderDetailItem,
            position: Int
        ) {
            holder.itemView.tv_folder_detail_title.text = item.title
            holder.itemView.setOnClickListener {
                //TODO 이미지 상세보기
                (viewModel as RecentlyItemViewModel).insert(RecentlyViewedItemDTO(
                    title = item.title,
                    date = System.currentTimeMillis().toString()
                ))
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

        inner class FolderDetailViewHolder(view: View): AndroidExtensionsViewHolder(view)
    }
}
