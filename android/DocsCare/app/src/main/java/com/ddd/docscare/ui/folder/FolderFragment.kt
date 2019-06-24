package com.ddd.docscare.ui.folder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.base.BaseFragment
import com.ddd.docscare.base.BaseRecyclerAdapter
import com.ddd.docscare.model.FolderItem
import com.ddd.docscare.ui.common.SpacesItemDecoration
import com.ddd.docscare.util.AndroidExtensionsViewHolder
import com.ddd.docscare.util.formatToServerDateTimeDefaults
import kotlinx.android.synthetic.main.fragment_folder.*
import kotlinx.android.synthetic.main.fragment_folder_item.view.*
import java.util.*

class FolderFragment: BaseFragment() {

    override val layoutId: Int = R.layout.fragment_folder
    private val adapter by lazy { FolderAdapter(requireContext()) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initLayout()
    }

    private fun initLayout() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(
            SpacesItemDecoration(
                resources.getDimensionPixelSize(R.dimen.folder_item_space), 3
            )
        )

        btnAddFolder.setOnClickListener {
            val tempFolderTitle = Date(System.currentTimeMillis()).formatToServerDateTimeDefaults()
            adapter.add(FolderItem(title = "Doc_$tempFolderTitle"))
            adapter.setFolderState("Doc_$tempFolderTitle", false)
            adapter.notifyDataSetChanged()
        }

        val categories = resources.getStringArray(R.array.category)
        for(category in categories) {
            adapter.add(FolderItem(title = category))
            adapter.setFolderState(category, true)
        }
        adapter.notifyDataSetChanged()
    }


    class FolderAdapter(private val context: Context): BaseRecyclerAdapter<FolderItem, FolderAdapter.FolderViewHolder>() {

        private val folderStateMap: HashMap<String, Boolean> by lazy { HashMap<String, Boolean>() }

        override fun onBindView(
            holder: FolderViewHolder,
            item: FolderItem,
            position: Int
        ) {
            val holderView = holder.itemView

            holderView.tv_folder_title.setText(item.title)

            folderStateMap[item.title]?.let {
                if(it) {
                    holderView.iv_folder_cancel.visibility = GONE
                } else {
                    holderView.iv_folder_cancel.visibility = VISIBLE
                    holderView.iv_folder_cancel.setOnClickListener {
                        remove(item)
                        notifyItemRemoved(holder.adapterPosition)
                        notifyItemRangeChanged(holder.adapterPosition, itemCount)
                    }

                    val old = holderView.tv_folder_title.text.toString()

                    holderView.tv_folder_title.isFocusableInTouchMode = true
                    (context as BaseActivity).showKeyboard(holderView.tv_folder_title)
                    holderView.tv_folder_title.setOnFocusChangeListener { v, hasFocus ->
                        if(!hasFocus) {
                            holderView.tv_folder_title.isFocusableInTouchMode = false
                            holderView.tv_folder_title.clearFocus()
                            context.hideKeyboard()
                            item.title = holderView.tv_folder_title.text.toString()

                            folderStateMap.remove(old)
                            setFolderState(holderView.tv_folder_title.text.toString(), true)

                            //TODO #BACKGROUND  1. title로 폴더만들기  2. 폴더-폴더ID DB 연동

                            notifyItemChanged(holder.adapterPosition)
                        }
                    }
                }
            }

            holderView.setOnClickListener {
                // TODO 폴더 상세보기 프레그먼트 전환
                Toast.makeText(context, "${holder.adapterPosition} 클릭", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, FolderDetailActivity::class.java))
            }


            when(holderView.tv_folder_title.text.toString()) {
                "커리어", "학업", "공공문서", "금융", "여행", "부동산" -> return
            }

            holderView.setOnLongClickListener {
                setFolderState(holderView.tv_folder_title.text.toString(), false)
                notifyItemChanged(holder.adapterPosition)
                true
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): FolderViewHolder {
            return FolderViewHolder(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.fragment_folder_item, parent, false)
            )
        }

        fun setFolderState(key: String, v: Boolean){
            folderStateMap[key] = v
        }

        class FolderViewHolder(view: View): AndroidExtensionsViewHolder(view)
    }

    companion object {
        fun newInstance(): FolderFragment {
            return FolderFragment()
        }
    }
}