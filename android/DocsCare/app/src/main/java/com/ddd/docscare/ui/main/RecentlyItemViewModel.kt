package com.ddd.docscare.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ddd.docscare.base.BaseViewModel
import com.ddd.docscare.db.dto.RecentlyViewedItemDTO
import com.ddd.docscare.repository.RecentlyViewRepository
import com.ddd.docscare.util.doAsync
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecentlyItemViewModel(private val recentlyViewRepository: RecentlyViewRepository):
        BaseViewModel() {

    private val _recentlyItems = MutableLiveData<List<RecentlyViewedItemDTO>>()
    val recentlyItems: LiveData<List<RecentlyViewedItemDTO>> = _recentlyItems

    fun selectTop5() {
        addDisposable(recentlyViewRepository.selectTop5()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _recentlyItems.postValue(it)
            })
    }

    fun insert(recentlyViewedItemDTO: RecentlyViewedItemDTO) {
        doAsync {
            recentlyViewRepository.insert(recentlyViewedItemDTO)
        }
    }
}