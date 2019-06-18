package com.ddd.docscare.repository.login

import com.ddd.docscare.model.ImageResponse
import com.ddd.docscare.model.UserInfo
import io.reactivex.Single

class LoginRepository(private val dataSource: LoginRemoteDataSource) {
    fun getImages(user_id: String, text: String): Single<List<ImageResponse>> =
            dataSource.getImages(user_id, text)

    fun addUser(user_id: String, userInfo: UserInfo): Single<UserInfo> =
            dataSource.addUser(user_id, userInfo)
}