package cn.shengwang.http.bean

/**
 * author : felix
 * date : 2024/4/15
 * description :
 */
data class FcrSceneJoinRoomReq(
    var roomId: String,
    var role: String,
    var userUuid: String,
    var userName: String,
)