package com.ddd.docscare.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ddd.docscare.base.BaseViewModel
import com.ddd.docscare.db.dto.FileItemDTO
import com.ddd.docscare.repository.file.FileRepository
import com.ddd.docscare.util.doAsync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FileViewModel(private val repository: FileRepository): BaseViewModel() {

    private val _filesResponse = MutableLiveData<List<FileItemDTO>>()
    val filesResponse: LiveData<List<FileItemDTO>>
        get() = _filesResponse

    private val _fileResponseByCategory = MutableLiveData<List<FileItemDTO>>()
    val fileResponseByCategory: LiveData<List<FileItemDTO>>
        get() = _fileResponseByCategory

    fun search(value: String) {
        addDisposable(repository.search(value)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _filesResponse.postValue(it)
            })
    }
    fun selectByCategory(category: String) {
        addDisposable(repository.selectByCategory(category)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _fileResponseByCategory.postValue(it)
            })
    }

    fun insert(fileItemDTO: FileItemDTO) {
        doAsync { repository.insert(fileItemDTO) }
    }
    fun delete(fileItemDTO: FileItemDTO) {
        doAsync { repository.delete(fileItemDTO) }
    }
}