package com.ddd.docscare.sns

import com.ddd.docscare.base.BaseApplication
import com.kakao.auth.*

class KakaoSdkAdapter: KakaoAdapter() {

    override fun getSessionConfig(): ISessionConfig {
        return object: ISessionConfig {
            // 웹뷰에서 email 입력폼 data를 저장할 지 여부
            override fun isSaveFormData(): Boolean = true

            // 로그인시 인증받을 타입
            override fun getAuthTypes(): Array<AuthType> = arrayOf(AuthType.KAKAO_LOGIN_ALL)

            // 로그인시 access, refresh token을 암호화하여 저장할지 여부
            override fun isSecureMode(): Boolean = true

            // 일반 사용자가 아닌 카카오와 제휴된 앱에서 사용되는 값으로 기본값은 ApprovalType.INDIVIDUAL
            override fun getApprovalType(): ApprovalType = ApprovalType.INDIVIDUAL

            // 웹뷰에서 pause와 resume 시 타이머를 설정하여 CPU 소모를 절약할지 여부
            override fun isUsingWebviewTimer(): Boolean = false
        }
    }

    // push 설정에 필요한 값 콜백을 받는다.
    override fun getPushConfig(): IPushConfig {
        return object: IPushConfig {
            override fun getTokenRegisterCallback(): ApiResponseCallback<Int> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getDeviceUUID(): String {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    // application이 가진 정보를 얻기위해서 반드시 구현해줘야 함
    override fun getApplicationConfig(): IApplicationConfig =
        IApplicationConfig { BaseApplication.getBaseApplicationContext() }

}