package com.ddd.docscare.ui.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ddd.docscare.base.BaseViewModel
import com.ddd.docscare.db.dto.FolderItemDTO
import com.ddd.docscare.repository.folder.FolderRepository
import com.ddd.docscare.util.doAsync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FolderViewModel(private val repository: FolderRepository): BaseViewModel() {

    private val _foldersResponse = MutableLiveData<List<FolderItemDTO>>()
    val foldersResponse: LiveData<List<FolderItemDTO>>
        get() = _foldersResponse

    private val _folderResponse = MutableLiveData<FolderItemDTO>()
    val folderResponse: LiveData<FolderItemDTO>
        get() = _folderResponse

    fun select() {
        addDisposable(repository.select()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _foldersResponse.postValue(it)
            })
    }
    fun selectByPath(path: String) {
        addDisposable(repository.selectByPath(path)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _folderResponse.postValue(it)
            })
    }

    fun insert(folderItemDTO: FolderItemDTO) {
        doAsync { repository.insert(folderItemDTO) }
    }
    fun delete(folderItemDTO: FolderItemDTO) {
        doAsync { repository.delete(folderItemDTO) }
    }
}