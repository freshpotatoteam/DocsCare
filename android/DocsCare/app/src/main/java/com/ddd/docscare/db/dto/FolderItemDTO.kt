package com.ddd.docscare.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folder_item")
data class FolderItemDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var path: String = "",          // 폴더경로
    var title: String = "",         // 폴더명
    var category: String = "",       // 카테고리
    var resourceId: Int = -1       // 폴더 아이콘 리소스
)