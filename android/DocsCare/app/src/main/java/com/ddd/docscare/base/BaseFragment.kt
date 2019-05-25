package com.ddd.docscare.base

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ddd.docscare.inject.Injection

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

    inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
        removeObserver(observer)
        observe(owner, observer)
    }
}