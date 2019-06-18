package com.ddd.docscare.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed_item")
data class RecentlyViewedItemDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val image: String = "",
    val title: String = "",
    val date: String = "",
    val type: String = ""
)