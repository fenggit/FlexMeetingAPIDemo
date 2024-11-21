package cn.shengwang.http.bean

/**
 * author : felix
 * date : 2024/4/15
 * description :
 */
data class FcrSceneCreateRoomReq(
    var roomName: String,
    var startTime: Long,
    var endTime: Long,
    var roomProperties: Map<String, String>? = null,
    var sceneType: Int = 4,
    var creatorUuid: String? = null // TODO 收回主持人需要
)

