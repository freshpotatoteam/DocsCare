package com.ddd.docscare.ui.result

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.ddd.docscare.R
import com.ddd.docscare.common.CATEGORY_MAP
import com.ddd.docscare.db.dto.FolderItemDTO
import kotlinx.android.synthetic.main.category_swipe_layout.view.*

class CategorySwiper @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0): LinearLayout(context, attrs, defStyleAttr) {

    lateinit var list: List<FolderItemDTO>
    lateinit var current: FolderItemDTO
    var currentNum: Int = 0

    init {
        initView()
    }

    private fun initView() {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.category_swipe_layout, this, false)
        addView(view)

        iv_left_arrow.setOnClickListener {
            if(currentNum == 0) {
                currentNum = list.size - 1
            } else {
                currentNum -= 1
            }

            iv_folder_icon.setImageDrawable(ContextCompat.getDrawable(context, list[currentNum].resourceId))
            category_title.text = list[currentNum].title
        }
        iv_right_arrow.setOnClickListener {
            if(currentNum == list.size -1) {
                currentNum = 0
            } else {
                currentNum += 1
            }

            iv_folder_icon.setImageDrawable(ContextCompat.getDrawable(context, list[currentNum].resourceId))
            category_title.text = list[currentNum].title
        }
    }

    fun setCategories(
        list: List<FolderItemDTO>,
        category: String
    ) {
        this.list = list
        current = this.list[currentNum]

        var count = 0
        for(item in list) {
            if(item.title == CATEGORY_MAP[category]) {
                break
            }
            count += 1
        }

        iv_folder_icon.setImageDrawable(ContextCompat.getDrawable(context, list[count].resourceId))
        category_title.text = list[count].title
    }

    fun getCurrentItem(): FolderItemDTO {
        return current
    }

}