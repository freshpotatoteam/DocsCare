package com.ddd.docscare.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

abstract class BaseFragment: ViewLifecycleFragment() {

    abstract val layoutId: Int

    lateinit var mActivity: BaseActivity
    lateinit var mContext: Context
    var supportActionBar: ActionBar?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = requireActivity() as BaseActivity
        mContext = requireContext()
        supportActionBar = mActivity.supportActionBar
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(layoutId, null, false)
    }

    fun showProgress() = mActivity.showProgress()
    fun dismissProgress() = mActivity.dismissProgress()
    fun dismissProgressForce() = mActivity.dismissProgressForce()
    fun hideKeyboard() = mActivity.hideKeyboard()
    fun showKeyboard(input: View) = mActivity.showKeyboard(input)

    fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
        removeObserver(observer)
        observe(owner, observer)
    }
}