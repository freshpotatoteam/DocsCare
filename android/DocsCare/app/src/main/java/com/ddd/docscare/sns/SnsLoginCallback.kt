package com.ddd.docscare.sns

import com.ddd.docscare.model.UserInfo

interface SnsLoginCallback {
    fun onSuccess(userInfo: UserInfo)
    fun onFailed(e: Exception)
}