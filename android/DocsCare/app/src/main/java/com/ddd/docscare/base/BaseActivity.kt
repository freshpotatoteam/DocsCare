package com.ddd.docscare.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ddd.docscare.R

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

    fun showKeyboard(input: View) {
        input.requestFocus()
        input.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(input, 0)
        }, 30)
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

    fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
        removeObserver(observer)
        observe(owner, observer)
    }
}