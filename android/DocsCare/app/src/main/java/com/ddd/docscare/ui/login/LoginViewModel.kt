package com.ddd.docscare.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ddd.docscare.base.BaseViewModel
import com.ddd.docscare.common.UserState
import com.ddd.docscare.model.UserInfo
import com.ddd.docscare.repository.AppRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginViewModel(private val repository: AppRepository): BaseViewModel() {

    private val _loginResponse = MutableLiveData<UserState>()
    val loginResponse: LiveData<UserState>
        get() = _loginResponse

    fun doSignUp(user_id: String, userInfo: UserInfo) {
        addDisposable(repository.addUser(user_id, userInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ userInfo ->
                _loginResponse.postValue(UserState.onSuccess(userInfo))
            }, { e ->
                e.printStackTrace()
                _loginResponse.postValue(UserState.onFailed("sign up failed.. ${e.message}"))
            }))
    }
}