package com.ddd.docscare.repository

import com.ddd.docscare.model.ImageResponse
import com.ddd.docscare.model.UserInfo
import com.ddd.docscare.network.ApiService
import io.reactivex.Single

class RemoteRepository(private val apiService: ApiService): AppDataSource {

    override fun getImages(user_id: String, text: String): Single<List<ImageResponse>> {
        return apiService.getImages(user_id, text)
    }

    override fun addUser(user_id: String, userInfo: UserInfo): Single<UserInfo> {
        return apiService.addUser(user_id, userInfo)
    }
}