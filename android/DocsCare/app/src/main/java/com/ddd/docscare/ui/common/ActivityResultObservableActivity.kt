package com.ddd.docscare.ui.common

import android.content.Intent
import com.ddd.docscare.base.BaseActivity

/**
 * https://medium.com/@pivincii/onactivityresult-in-a-custom-view-d6036365d5d4
 * Activity
 */
abstract class ActivityResultObservableActivity: BaseActivity(),
    ActivityResultObservable {
    private val activityObserverList = mutableListOf<ActivityResultObserver>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityObserverList.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }

    override fun addObserver(activityResultObserver: ActivityResultObserver) {
        activityObserverList.add(activityResultObserver)
    }

    override fun removeObserver(activityResultObserver: ActivityResultObserver) {
        activityObserverList.remove(activityResultObserver)
    }

}