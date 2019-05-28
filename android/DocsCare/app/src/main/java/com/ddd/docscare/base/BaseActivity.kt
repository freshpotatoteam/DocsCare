package com.ddd.docscare.base

import com.ddd.docscare.R
import android.arch.lifecycle.*
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import com.ddd.docscare.inject.Injection

open class BaseActivity: AppCompatActivity() {

    lateinit var mActivity: BaseActivity
    lateinit var mContext: Context
    private var destroied: Boolean = false

    private val mProgress by lazy { createProgress() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = this
        mContext = this
    }

    override fun onDestroy() {
        super.onDestroy()
        destroied = true
    }

    private fun createProgress(): AppCompatDialog {
        return with(AlertDialog.Builder(this)) {
            setCancelable(true)
            setView(ProgressBar(context, null, R.attr.progressBarStyle))
            create().apply {
                window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.translucent))
                setCanceledOnTouchOutside(false)
            }
        }
    }

    fun hideKeyboard() {
        currentFocus?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun showProgress() {
        if (lifecycle.currentState === Lifecycle.State.DESTROYED) return
        if (isFinishing) return
        if (destroied) return

        mProgress.takeUnless { mProgress.isShowing }?.show()
    }

    fun dismissProgress() {
        mProgress.takeIf { mProgress.isShowing }?.dismiss()
    }

    fun dismissProgressForce() {
        dismissProgress()
    }

    inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
        removeObserver(observer)
        observe(owner, observer)
    }
}