package com.ddd.docscare.ui.common

interface ActivityResultObservable {
    fun addObserver(activityResultObserver: ActivityResultObserver)
    fun removeObserver(activityResultObserver: ActivityResultObserver)
}