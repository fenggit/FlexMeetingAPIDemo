package io.agora.educore.example

import android.app.Application
import io.agora.agoracore.core2.utils.FcrSDKInitUtils

/**
 * author : felix
 * date : 2024/3/7
 * description :
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FcrSDKInitUtils.initSDK(this)
    }
}