package com.ddd.docscare.sns

import android.app.Activity
import com.ddd.docscare.BuildConfig.NAVER_AUTH_CLIENT_ID
import com.ddd.docscare.BuildConfig.NAVER_AUTH_SECRET
import com.ddd.docscare.R
import com.ddd.docscare.model.UserInfo
import com.ddd.docscare.util.doAsync
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import org.json.JSONObject
import java.lang.ref.WeakReference

class NaverLoginHelper(private val activity: Activity,
                       private val callback: SnsLoginCallback) {

    private val loginHelper: WeakReference<NaverLoginHelper> = WeakReference(this)
    private var loginModule: OAuthLogin
    private var loginHandler: OAuthLoginHandler

    init {
        loginModule = OAuthLogin.getInstance()
        loginModule.init(activity, NAVER_AUTH_CLIENT_ID, NAVER_AUTH_SECRET, activity.getString(R.string.app_name))
        loginHandler = NaverLoginHandler(loginHelper, loginModule, callback)
    }

    fun startOAuthLoginActivity() {
        loginModule.startOauthLoginActivity(activity, loginHandler)
    }

    // 클라이언트에 저장된 토큰이 삭제되고 OAuthLogin.getState() 메서드가 OAuthLoginState.NEED_LOGIN 값을 반환
    fun logout() {
        loginModule.logout(activity)
    }

    // 네이버 아이디와 애플리케이션의 연동을 해제
    // 연동을 해제하면 클라이언트에 저장된 토큰과 서버에 저장된 토큰이 모두 삭제
    fun logoutAndDeleteToken() {
        doAsync {
            if(!loginModule.logoutAndDeleteToken(activity)) {
                println("errorCode ${loginModule.getLastErrorCode(activity)}")
                println("errorDesc ${loginModule.getLastErrorDesc(activity)}")
            }
        }
    }


    class NaverLoginHandler(private val outerClass: WeakReference<NaverLoginHelper>,
                            private val module: OAuthLogin,
                            private val callback: SnsLoginCallback): OAuthLoginHandler() {

        override fun run(success: Boolean) {
            outerClass.get()?.let {
                if(success) {
                    val accessToken = module.getAccessToken(it.activity)
                    println("accessToken $accessToken")
                    println("refreshToken ${module.getRefreshToken(it.activity)}")
                    println("expiresAt ${module.getExpiresAt(it.activity)}")
                    println("tokenType ${module.getTokenType(it.activity)}")

                    doAsync {
                        // https://developers.naver.com/docs/login/devguide/#3-4-5-%EC%A0%91%EA%B7%BC-%ED%86%A0%ED%81%B0%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC-%ED%94%84%EB%A1%9C%ED%95%84-api-%ED%98%B8%EC%B6%9C%ED%95%98%EA%B8%B0
                        val me = module.requestApi(it.activity, accessToken, NAVER_OAUTH_PRFILE_URL)
                        println("me $me")
                        val jsonObject = JSONObject(me)
                        val response = jsonObject.getJSONObject("response")
                        val id = response.getString("id")
                        val nickname = response.getString("nickname")
                        val profile_image = response.getString("profile_image")
                        val name = response.getString("name")
                        println("id : $id \n nickname : $nickname \n image : $profile_image \n name : $name")

                        // 이후 회원가입 요청
                        callback.onSuccess(
                            UserInfo(user_id = id,
                                nickname = nickname,
                                profile_image_path = profile_image)
                        )
                    }
                } else {
                    println("errorCode ${module.getLastErrorCode(it.activity)}")
                    println("errorDesc ${module.getLastErrorDesc(it.activity)}")
                }
            }
        }
    }

    companion object {
        const val NAVER_OAUTH_PRFILE_URL = "https://openapi.naver.com/v1/nid/me"
    }

}