package com.ddd.docscare.inject

object Injection {
    fun provideViewModelFactory(): ViewModelFactory {
        return ViewModelFactory()
    }
}