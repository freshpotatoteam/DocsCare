package com.ddd.docscare.ui.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ddd.docscare.base.BaseViewModel
import com.ddd.docscare.model.ImageResponse
import com.ddd.docscare.repository.scan.ScanRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class ScanViewModel(private val repository: ScanRepository): BaseViewModel() {

    private val _imageResponse = MutableLiveData<ImageResponse>()
    val imageResponse: LiveData<ImageResponse>
        get() = _imageResponse

    // 스캔된 이미지 서버 전달 : 카테고리 받기
    fun uploadImage(user_id: String,
                    file: MultipartBody.Part,
                    image_name: String) {
        addDisposable(repository.uploadImage(user_id, file, image_name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                _imageResponse.postValue(it)
            }, { e ->
                e.printStackTrace()
            }))
    }


    // 스캔된 이미지 저장
    // 스캔된 이미지 uri db 캐시?
    // 스캔된 이미지 폴더 이동
}