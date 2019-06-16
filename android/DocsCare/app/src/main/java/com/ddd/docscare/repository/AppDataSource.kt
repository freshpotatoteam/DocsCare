package com.ddd.docscare.repository

import com.ddd.docscare.model.ImageResponse
import com.ddd.docscare.model.UserInfo
import io.reactivex.Single

interface AppDataSource {
    fun getImages(user_id: String, text: String): Single<List<ImageResponse>>

    fun addUser(user_id: String, userInfo: UserInfo): Single<UserInfo>
}