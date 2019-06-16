package com.ddd.docscare.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.ddd.docscare.MainActivity
import com.ddd.docscare.R
import com.ddd.docscare.base.BaseActivity
import com.ddd.docscare.model.UserInfo
import com.ddd.docscare.sns.KakaoLoginHelper
import com.ddd.docscare.sns.NaverLoginHelper
import com.ddd.docscare.sns.SnsLoginCallback
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initLayout()
        observe()
    }

    private fun initLayout() {
        val snsLoginCallback = object: SnsLoginCallback {
            override fun onSuccess(userInfo: UserInfo) {
                viewModel.doSignUp(userInfo.user_id, userInfo)
            }

            override fun onFailed(e: Exception) {
                e.printStackTrace()
            }
        }

        naverLogin.setOnClickListener {
            NaverLoginHelper(this@LoginActivity, snsLoginCallback).startOAuthLoginActivity()
        }

        kakaoLogin.setOnClickListener {
            KakaoLoginHelper(this@LoginActivity, snsLoginCallback).startOAuth()
        }
    }

    private fun observe() {
        viewModel.loginResponse.observe(this, Observer {
            println("user_id : ${it.user_id}  nickname: ${it.nickname}")
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        })
    }
}
