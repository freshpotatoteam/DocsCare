package com.ddd.docscare.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.support.multidex.MultiDexApplication
import android.util.Base64
import android.util.Log
import com.ddd.docscare.sns.KakaoSdkAdapter
import com.kakao.auth.KakaoSDK
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class BaseApplication: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        val debugKeyHash = getKeyHash()
        Log.d("Debug Key Hash", debugKeyHash)

        KakaoSDK.init(KakaoSdkAdapter())
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