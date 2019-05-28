package com.ddd.docscare.ui

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.base.BaseRecyclerAdapter
import com.ddd.docscare.ui.folder.FolderFragment
import kotlinx.android.synthetic.main.activity_recently_used_item.view.*
import kotlinx.android.synthetic.main.main.*

class Main : BaseActivity() {

    private val adapter by lazy { RecentlyUsedItemAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        initLayout()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if(ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if(view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if(outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun initLayout() {
        recentlyUsedView.adapter = adapter
        recentlyUsedView.layoutManager = LinearLayoutManager(this)
        recentlyUsedView.isNestedScrollingEnabled = false

        adapter.add(RecentlyUsedItem(title = "최근문서"))
        adapter.add(RecentlyUsedItem(title = "DocsCare_ 2019.06.03"))
        adapter.add(RecentlyUsedItem(title = "DocsCare_ 2019.06.04"))
        adapter.add(RecentlyUsedItem(title = "DocsCare_ 2019.06.05"))
        adapter.add(RecentlyUsedItem(title = "DocsCare_ 2019.06.06"))
        adapter.add(RecentlyUsedItem(title = "DocsCare_ 2019.06.07"))
        adapter.notifyDataSetChanged()

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.mainFrame,
                FolderFragment.newInstance()
            )
            .commit()
    }


    class RecentlyUsedItemAdapter: BaseRecyclerAdapter<RecentlyUsedItem, RecyclerView.ViewHolder>() {

        override fun onBindView(
            holder: RecyclerView.ViewHolder,
            item: RecentlyUsedItem,
            position: Int
        ) {
            val holderView = holder.itemView

            holderView.tv_recently_title.text = item.title
        }

        override fun getItemViewType(position: Int): Int {
            return if(position == 0) 0 else 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
            return if(type == 0) {
                RecentlyUsedHeaderViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.activity_recently_used_item,
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
    }

    class RecentlyUsedHeaderViewHolder(view: View): RecyclerView.ViewHolder(view)
    class RecentlyUsedViewHolder(view: View): RecyclerView.ViewHolder(view)
}
