package com.ddd.docscare.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "file_item")
data class FileItemDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var path: String = "",          // 파일경로
    var title: String = "",         // 파일명
    var category: String = ""       // 카테고리(폴더명)
)