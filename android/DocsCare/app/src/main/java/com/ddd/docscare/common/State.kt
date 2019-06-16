package com.ddd.docscare.common

import com.ddd.docscare.model.UserInfo

sealed class UserState {
    class onSuccess(val userInfo: UserInfo): UserState()
    class onFailed(val errMsg: String): UserState()
}