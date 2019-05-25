package com.ddd.docscare.inject

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(viewModelClass: Class<T>): T {
        return when(viewModelClass) {

            else -> throw IllegalArgumentException("unknown viewmodel class")
        }
    }
}