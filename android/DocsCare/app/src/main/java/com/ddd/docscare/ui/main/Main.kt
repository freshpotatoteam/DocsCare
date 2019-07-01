package com.ddd.docscare.ui.main

import android.Manifest
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseRecyclerAdapter
import com.ddd.docscare.common.DEFAULT_FOLDER_LIST
import com.ddd.docscare.common.DOCS_FOLDER_PATH
import com.ddd.docscare.db.dto.FolderItemDTO
import com.ddd.docscare.db.dto.RecentlyViewedItemDTO
import com.ddd.docscare.ui.common.ActivityResultObservableActivity
import com.ddd.docscare.ui.common.SpacesItemDecoration
import com.ddd.docscare.ui.folder.FolderFragment
import com.ddd.docscare.ui.folder.FolderViewModel
import com.ddd.docscare.util.AndroidExtensionsViewHolder
import com.ddd.docscare.util.onRightDrawableClicked
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_recently_used_header_item.view.*
import kotlinx.android.synthetic.main.activity_recently_used_item.view.*
import kotlinx.android.synthetic.main.main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.lang.Exception

class Main : ActivityResultObservableActivity() {

    private val viewModel: RecentlyItemViewModel by viewModel()
    private val folderViewModel: FolderViewModel by viewModel()
    private val adapter by lazy { RecentlyUsedItemAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val permissionListener =  object: PermissionListener {
            override fun onPermissionGranted() {
                initLayout()
                createDefaultFolder()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                println("permission denied")
            }

        }

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("거절할 경우 세팅화면에서 수동으로 설정")
            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("###main onActivityResult")
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if(ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if(view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if(!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    hideKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun initLayout() {
        recentlyUsedView.adapter = adapter
        recentlyUsedView.layoutManager = LinearLayoutManager(this)
        recentlyUsedView.addItemDecoration(
            SpacesItemDecoration(
                resources.getDimensionPixelSize(R.dimen.recently_item_space)
            )
        )
        recentlyUsedView.isNestedScrollingEnabled = false


        supportFragmentManager.beginTransaction()
            .replace(
                R.id.mainFrame,
                FolderFragment.newInstance()
            )
            .commit()


        edtSearch.onRightDrawableClicked {
            it.text.clear()
            it.clearFocus()
            hideKeyboard()
            //TODO 폴더 및 파일 검색 (Rx 사용, debounce)
        }

        setScrollEvent()
        loadRecentlyViewedItem()
    }

    private fun createDefaultFolder() {
        val rootFolder = File(DOCS_FOLDER_PATH)
        try {
            if (rootFolder.mkdir()) {
                for (folderInfo in DEFAULT_FOLDER_LIST) {
                    val isSuccess = File(folderInfo.first).mkdir()
                    if (isSuccess)
                        folderViewModel.insert(
                            FolderItemDTO(
                                path = folderInfo.first,
                                title = folderInfo.second,
                                resourceId = folderInfo.third
                            )
                        )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setScrollEvent() {
        nestedScrollView.setScrollListener {
            when(it) {
                RecyclerView.SCROLL_STATE_DRAGGING -> { bottomNavigationView.visibility = GONE }
                RecyclerView.SCROLL_STATE_IDLE -> { bottomNavigationView.visibility = VISIBLE }
            }
        }
    }

    private fun loadRecentlyViewedItem() {
        viewModel.recentlyItems.observe(this, Observer {
            adapter.removeAll()
            adapter.add(RecentlyViewedItemDTO(title = "title"))
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
        })

        viewModel.selectTop5()
    }


    class RecentlyUsedItemAdapter: BaseRecyclerAdapter<RecentlyViewedItemDTO, RecyclerView.ViewHolder>() {

        override fun onBindView(
            holder: RecyclerView.ViewHolder,
            item: RecentlyViewedItemDTO,
            position: Int
        ) {
            when(holder) {
                is RecentlyUsedHeaderViewHolder -> {
                    val holderView = holder.itemView
                    holderView.tv_recently_header_title.text = "최근문서"
                }

                is RecentlyUsedViewHolder -> {
                    val holderView = holder.itemView
                    holderView.tv_recently_doc_title.text = item.title
                }
            }

        }

        override fun getItemViewType(position: Int): Int {
            return if(position == 0) 0 else 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
            return if(type == 0) {
                RecentlyUsedHeaderViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.activity_recently_used_header_item,
                        parent,
                        false
                    )
                )
            } else {
                RecentlyUsedViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.activity_recently_used_item,
                        parent,
                        false
                    )
                )
            }
        }

        inner class RecentlyUsedHeaderViewHolder(view: View): AndroidExtensionsViewHolder(view)
        inner class RecentlyUsedViewHolder(view: View): AndroidExtensionsViewHolder(view)
    }
}
