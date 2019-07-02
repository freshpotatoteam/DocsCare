package com.ddd.docscare.repository.scan

import com.ddd.docscare.model.ImageResponse
import com.ddd.docscare.network.ApiService
import io.reactivex.Single
import okhttp3.MultipartBody

class ScanRemoteDataSource(private val apiService: ApiService) {
    // 스캔된 이미지 서버 전달 : 카테고리 받기
    fun uploadImage(user_id: String,
                    file: MultipartBody.Part,
                    image_name: String): Single<ImageResponse> {
        return apiService.uploadImage(user_id, file, image_name)
    }
}