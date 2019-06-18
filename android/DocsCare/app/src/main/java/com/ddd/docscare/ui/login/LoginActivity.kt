package com.ddd.docscare.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.base.PP
import com.ddd.docscare.common.UserState
import com.ddd.docscare.model.UserInfo
import com.ddd.docscare.sns.KakaoLoginHelper
import com.ddd.docscare.sns.NaverLoginHelper
import com.ddd.docscare.sns.SnsLoginCallback
import com.ddd.docscare.ui.Main
import com.kakao.auth.Session
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity() {

    private val viewModel: LoginViewModel by viewModel()

    private val naverHelper by lazy { NaverLoginHelper(this@LoginActivity, snsLoginCallback) }
    private val kakaoHelper by lazy { KakaoLoginHelper(this@LoginActivity, snsLoginCallback) }
    private val snsLoginCallback = object: SnsLoginCallback {
        override fun onSuccess(userInfo: UserInfo) {
            viewModel.doSignUp(userInfo.user_id, userInfo)
        }

        override fun onFailed(e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initLayout()
        observe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        kakaoHelper.removeCallback()
    }

    private fun initLayout() {

        val userId = PP.USER_ID.getString()
        if(userId.isNullOrEmpty()) {
            naverLogin.setOnClickListener {
                naverHelper.startOAuthLoginActivity()
            }

            kakaoLogin.setOnClickListener {
                kakaoHelper.startOAuth()
            }
        } else {
            startActivity(Intent(this@LoginActivity, Main::class.java))
            finish()
        }
    }

    private fun observe() {
        viewModel.loginResponse.observe(this, Observer {
            when(it) {
                is UserState.onSuccess -> {
                    PP.USER_ID.set(it.userInfo.user_id)
                    startActivity(Intent(this@LoginActivity, Main::class.java))
                    finish()
                }

                is UserState.onFailed -> {
                    println("$it")
                }
            }
        })
    }
}
