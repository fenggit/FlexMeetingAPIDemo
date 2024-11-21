package cn.shengwang.scene

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import io.agora.agoracore.core2.FcrCoreEngine
import io.agora.agoracore.core2.FcrCoreEngineImpl
import io.agora.agoracore.core2.bean.FcrCoreEngineConfig
import io.agora.core.common.log.LogX

/**
 * author : felix
 * date : 2024/3/5
 * description :
 */
object FcrUIScene {
    const val UI_SCENE_PRE_CHECK = "preCheck"
    const val UI_SCENE_CONFIG = "sceneConfig"
    val TAG = "FcrUIScene"

    fun startScene(context: Activity, sceneClass: Class<*>, config: FcrUISceneConfig) {
        LogX.e(TAG, "start config= $config")
        val intent = Intent(context, sceneClass)
        intent.putExtra(UI_SCENE_CONFIG, config)
        context.startActivity(intent)
    }

    fun getFcrUISceneConfig(context: Activity): FcrUISceneConfig? {
        return context.intent.getSerializableExtra(UI_SCENE_CONFIG) as FcrUISceneConfig?
    }

    fun createEngine(context: Activity, sceneConfig: FcrUISceneConfig): FcrCoreEngine {
        if (TextUtils.isEmpty(sceneConfig.logFileFolderPath)) {
            sceneConfig.logFileFolderPath = LogX.logDir
        }

        val config = FcrCoreEngineConfig(
            sceneConfig.appId,
            sceneConfig.token,
            sceneConfig.region,
            sceneConfig.userId,
            sceneConfig.dualCameraVideoStreamConfig,
            sceneConfig.parameters,
        )
        return FcrCoreEngineImpl(context.applicationContext, config)
    }
}