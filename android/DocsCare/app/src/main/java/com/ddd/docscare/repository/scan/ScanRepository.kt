package com.ddd.docscare.repository.scan

import com.ddd.docscare.model.ImageResponse
import io.reactivex.Single
import okhttp3.MultipartBody

class ScanRepository(private val localDataSource: ScanLocalDataSource,
                     private val remoteDataSource: ScanRemoteDataSource) {

    // 스캔된 이미지 저장
    // 스캔된 이미지 uri db 캐시?
    // 스캔된 이미지 폴더 이동

    // 스캔된 이미지 서버 전달 : 카테고리 받기
    fun uploadImage(user_id: String,
                    file: MultipartBody.Part,
                    image_name: String): Single<ImageResponse> {
        return remoteDataSource.uploadImage(user_id, file, image_name)
    }

}