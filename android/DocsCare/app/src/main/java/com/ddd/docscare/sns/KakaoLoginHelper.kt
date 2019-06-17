package com.ddd.docscare.sns

import android.app.Activity
import com.ddd.docscare.model.UserInfo
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException

class KakaoLoginHelper(private val activity: Activity,
                       private val snsLoginCallback: SnsLoginCallback) {

    private val sessionCallback = object: ISessionCallback {
        // 로그인 실패 상태
        override fun onSessionOpenFailed(exception: KakaoException?) {
            exception?.let { snsLoginCallback.onFailed(it) }
        }

        // 로그인 성공 상태
        override fun onSessionOpened() {
            requestMe()
        }
    }

    fun startOAuth() {
        Session.getCurrentSession().addCallback(sessionCallback)
        Session.getCurrentSession().checkAndImplicitOpen()
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, activity)
    }

    private fun requestMe() {
        val keys = arrayListOf("properties.nickname", "properties.profile_image", "properties.thumbnail_image")
        UserManagement.getInstance().me(keys, object: MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response) {
                snsLoginCallback.onSuccess(
                    UserInfo(user_id = result.id.toString(),
                        nickname = result.nickname,
                        profile_image_path = result.profileImagePath,
                        thumbnail_image_path = result.thumbnailImagePath
                    )
                )
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                // TODO Redirect Login.
            }
        })
    }
}