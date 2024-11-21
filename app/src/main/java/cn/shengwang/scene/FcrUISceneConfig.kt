package cn.shengwang.scene

import io.agora.agoracore.core2.bean.FcrDualVideoStreamConfig
import io.agora.agoracore.core2.bean.FcrStreamLatencyLevel
import io.agora.agoracore.core2.bean.FcrUserRole
import io.agora.core.common.http.bean.FcrRegion
import java.io.Serializable

/**
 * author : felix
 * date : 2024/1/23
 * description :
 */
data class FcrUISceneConfig(
    var appId: String,
    var token: String,
    var roomId: String,
    var userId: String,
    var userName: String,
    var userRole: FcrUserRole,
) : Serializable {
    /**
     * 设置区域
     */
    var region = FcrRegion.CN
    //var audioConfig: FcrAudioConfig? = null // 	音频配置
    var dualCameraVideoStreamConfig: FcrDualVideoStreamConfig? = null // 	摄像头视频多流配置
    var parameters: Map<String, Any>? = null // 	传入私有参数

    //===========need============
    var logFileFolderPath: String? = null
    var streamLatency = FcrStreamLatencyLevel.ULTRALOW

    var userProperties: Map<String, String>? = null
    var roomName: String = "" // 房间名称
    var startTime: Long = 0L // 开始时间
    var endTime: Long = 0L   // 结束时间
}