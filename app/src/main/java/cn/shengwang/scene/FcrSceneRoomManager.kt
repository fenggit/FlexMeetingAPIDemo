package cn.shengwang.scene

import io.agora.agoracore.core2.FcrCoreEngine
import io.agora.agoracore.core2.bean.FcrRoomJoinOptions
import io.agora.agoracore.core2.bean.FcrStreamJoinConfig
import io.agora.agoracore.core2.bean.FcrStreamLatencyLevel
import io.agora.agoracore.core2.bean.FcrStreamType
import io.agora.agoracore.core2.control.api.FcrBaseRoomControl
import io.agora.agoracore.core2.control.privilege.FcrPermissionAction
import io.agora.agoracore.core2.control.privilege.FcrSecurityAction
import io.agora.agoracore.core2.http.FcrResource
import io.agora.agoracore.rte2.cmd.manager.AgoraRoomStateManager

/**
 * author : qinwei@agora.io
 * date : 2024/10/8 14:28
 * description :房间管理
 */
object FcrSceneRoomManager {

    /**
     * 加入会议
     * @param engine
     * @param config 会议配置信息
     */
    suspend fun joinRoom(engine: FcrCoreEngine, config: FcrUISceneConfig): FcrResource<FcrBaseRoomControl<*>> {
        val roomId = config.roomId
        val videoSourceId = engine.getMobileMediaControl().getCameraInfo().deviceId
        val audioSourceId = engine.getMobileMediaControl().getMicInfo().deviceId
        val createStreamConfigs = ArrayList<FcrStreamJoinConfig>()
        val streamInfo = FcrStreamJoinConfig(null, videoSourceId, audioSourceId, "Tom Stream", FcrStreamType.BOTH)
        createStreamConfigs.add(streamInfo)
        val option = FcrRoomJoinOptions(
            config.userName,
            config.userRole,
            config.userProperties,
            config.token,
            FcrStreamLatencyLevel.ULTRALOW,
            createStreamConfigs
        )
        return engine.createRoomControlAndJoin(roomId, option)
    }

    suspend fun joinWaitingRoom(
        engine: FcrCoreEngine,
        roomId: String,
        options: FcrRoomJoinOptions
    ): FcrResource<FcrBaseRoomControl<*>> {
        return engine.createRoomControlAndJoin(roomId, options)
    }

    fun getWaitingRoomId(engine: FcrCoreEngine, roomId: String): String? {
        val waitingRoomId = AgoraRoomStateManager.getWaitingRoomId(engine, roomId)
        if (isWaitingRoomEnable(engine, roomId)) {
            return waitingRoomId
        }
        return null
    }

    private fun isWaitingRoomEnable(engine: FcrCoreEngine, roomId: String): Boolean {
        val privilege = engine.getRoomControl(roomId)?.getPrivilegeControl()
        //等候室权限
        val waitingRoomEnable = privilege?.getSecurityInfo(FcrSecurityAction.WAITING_ROOM)?.enable
        //等候室管理权限
        val permission = privilege?.getLocalUserPermissionInfo(FcrPermissionAction.WAITING_ROOM_ENABLE)?.enable
        return waitingRoomEnable == true && permission == true
    }
}