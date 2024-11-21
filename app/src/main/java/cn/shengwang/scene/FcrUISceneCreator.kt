package cn.shengwang.scene

import android.app.Activity
import android.content.Intent
import cn.shengwang.scene.helper.FcrHttpHelper
import io.agora.agoracore.core2.utils.FcrSDKInitUtils
import io.agora.core.common.log.LogX
import java.util.concurrent.ConcurrentHashMap

/**
 * author : felix
 * date : 2024/1/23
 * description :
 */
object FcrUISceneCreator {
    private var observerMap = ConcurrentHashMap<String, FcrUISceneObserver>()

    /**
     * 被踢出
     */
    val SCENE_STATE_KICK_OUT = 30403100

    /**
     * 课堂已结束
     */
    val SCENE_STATE_END = 20410100

    /**
     * 课堂已满
     */
    val SCENE_STATE_FULL = 20403001

    /**
     * 启动场景
     */
    fun launch(context: Activity, config: FcrUISceneConfig, sceneClass: Class<*>) {
        FcrSDKInitUtils.initSDK(context.applicationContext)
        FcrHttpHelper.initHttp(config)

        LogX.i(FcrUIScene.TAG, "start config= $config")
        // TODO 是否创建 Task
        val intent = Intent(context, sceneClass)
        intent.putExtra(FcrUIScene.UI_SCENE_CONFIG, config)
        context.startActivity(intent)
    }

    fun exit() {
        observerMap.clear()
    }

    fun addObserverOfUIScene(roomId: String, observer: FcrUISceneObserver) {
        observerMap[roomId] = observer
    }

    fun removeObserverOfUIScene(roomId: String) {
        observerMap.remove(roomId)
    }

    fun getObserverOfUIScene(): Map<String, FcrUISceneObserver> {
        return observerMap
    }
}