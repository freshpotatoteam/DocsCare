package com.ddd.docscare.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ddd.docscare.base.BaseViewModel
import com.ddd.docscare.model.UserInfo
import com.ddd.docscare.repository.AppDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginViewModel(private val repository: AppDataSource): BaseViewModel() {

    private val _loginResponse = MutableLiveData<UserInfo>()
    val loginResponse: LiveData<UserInfo>
        get() = _loginResponse

    fun doSignUp(user_id: String, userInfo: UserInfo) {
        addDisposable(repository.addUser(user_id, userInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { userInfo ->
                _loginResponse.postValue(userInfo)
            })
    }
}