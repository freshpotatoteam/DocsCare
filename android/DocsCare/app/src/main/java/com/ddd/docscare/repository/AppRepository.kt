package com.ddd.docscare.repository

import com.ddd.docscare.model.ImageResponse
import com.ddd.docscare.model.UserInfo
import io.reactivex.Single

class AppRepository(private val remoteAppDataSource: RemoteAppDataSource) {

    fun getImages(user_id: String, text: String): Single<List<ImageResponse>> =
        remoteAppDataSource.getImages(user_id, text)

    fun addUser(user_id: String, userInfo: UserInfo): Single<UserInfo> =
        remoteAppDataSource.addUser(user_id, userInfo)

}