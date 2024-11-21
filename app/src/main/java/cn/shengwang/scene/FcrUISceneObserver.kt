package cn.shengwang.scene

import io.agora.core.common.obs.FcrError

/**
 * author : felix
 * date : 2024/1/23
 * description :
 */
interface FcrUISceneObserver {
    /**
     * 退出
     */
    fun onExited(roomId: String, reason: FcrUIExitReason) {}

    /**
     * 进入成功
     */
    fun onJoinSucceed(roomId: String) {}

    /**
     * 进入失败
     */
    fun onJoinFailed(roomId: String, error: FcrError) {}
}