package com.ddd.docscare.base

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel: ViewModel() {
    protected val disposables = CompositeDisposable()
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}