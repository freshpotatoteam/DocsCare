package com.ddd.docscare.model

data class ImageResponse(
    val user_id: String = "",
    val image_name: String = "",
    val image_text: String = "",
    val image_thumbnail_url: String = "",
    val image_url: String = "",
    val category_id: String = "",
    val insert_datetime: String = "",
    val update_datetime: String = ""
)