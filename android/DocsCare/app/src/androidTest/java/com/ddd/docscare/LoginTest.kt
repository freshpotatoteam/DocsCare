package com.ddd.docscare

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.Toast
import com.ddd.docscare.sns.NaverLoginHelper
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    var rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun naverLoginTest() {
        val activity = rule.activity
        activity.runOnUiThread {
            NaverLoginHelper(activity).startOAuthLoginActivity()
        }

        Thread.sleep(150000)
    }

    @Test
    fun kakaoLoginTest() {
        val activity = rule.activity
        val sessionCallback = object: ISessionCallback {
            // 로그인 실패 상태
            override fun onSessionOpenFailed(exception: KakaoException?) {
                activity.runOnUiThread {
                    Toast.makeText(activity, "로그인 실패 ${exception?.errorType?.name}", Toast.LENGTH_SHORT).show()
                }
            }

            // 로그인 성공 상태
            override fun onSessionOpened() {
                activity.runOnUiThread {
                    Toast.makeText(activity, "로그인 성공", Toast.LENGTH_SHORT).show()
                }
                requestMe(activity)
            }
        }
        Session.getCurrentSession().addCallback(sessionCallback)

        Session.getCurrentSession().checkAndImplicitOpen()
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, activity)

        Thread.sleep(150000)
    }

    fun requestMe(activity: Activity) {
        val keys = arrayListOf("properties.nickname", "properties.profile_image", "properties.thumbnail_image")
        UserManagement.getInstance().me(keys, object: MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response?) {
                activity.runOnUiThread {
                    Toast.makeText(activity,
                        "#2 id:${result?.id}  가입여부:${result?.hasSignedUp()} nickname:${result?.nickname}",
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                // TODO Redirect Login.
            }
        })
    }
}