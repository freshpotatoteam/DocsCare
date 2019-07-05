package com.ddd.docscare.base

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.ddd.docscare.inject.appModule
import com.ddd.docscare.sns.KakaoSdkAdapter
import com.kakao.auth.KakaoSDK
import org.koin.android.ext.android.startKoin
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class BaseApplication: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        initPreference()
        initKakao()
        injectDependency()
        showDebugKeyHash()
    }

    private fun initPreference() {
        PP.CREATE(this)

        if(PP.FOLDER_NUM.getInt() == -1) {
            PP.FOLDER_NUM.set(0)
        }

        if(PP.FILE_NUM.getInt() == -1) {
            PP.FILE_NUM.set(0)
        }
    }

    private fun initKakao() {
        instance = this
        KakaoSDK.init(KakaoSdkAdapter())
    }

    private fun injectDependency() {
        startKoin(this, appModule)
    }

    private fun showDebugKeyHash() {
        val debugKeyHash = getKeyHash()
        Log.d("Debug Key Hash", debugKeyHash)
    }


    @SuppressLint("WrongConstant")
    fun getKeyHash(): String? {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                val info: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                val signatures: Array<Signature> = info.signingInfo.apkContentsSigners
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                for (signature in signatures) {
                    md.update(signature.toByteArray())
                    return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                }
            } else {
                val info: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                for(signature in info.signatures) {
                    md.update(signature.toByteArray())
                    return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }

        return null
    }


    companion object {
        private var instance: BaseApplication? = null
        fun getBaseApplicationContext() = instance ?: BaseApplication().also { instance = it }
    }
}