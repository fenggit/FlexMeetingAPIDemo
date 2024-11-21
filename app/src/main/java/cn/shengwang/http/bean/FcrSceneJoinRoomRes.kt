package cn.shengwang.http.bean

/**
 * author : felix
 * date : 2024/4/15
 * description :
 */
data class FcrSceneJoinRoomRes(
    var token: String,
    var appId: String,
    var role: String,
    var roomDetail: FcrSceneRoomDetail
)

data class FcrSceneRoomDetail(
    var roomName: String,
    var roomId: String,
    /**
     * 课堂状态 [0：未开始     1：进行中  2：已结束]
     */
    var roomState: Int,
    /**
     * 场景类型
     * 0：one_on_one     roomType = 0
     * 2：large_class    roomType = 2
     * 4：edu_medium_v1  roomType = 4
     * 6：proctoring     roomType = 6
     * 10: cloud_class   roomType = 4
     */
    var sceneType: Int = 4,
    var startTime: Long,
    var endTime: Long,
    var userName: String,
    var roomProperties: Map<String, Any>? = null
) {
    /**
     * 房间类型     [0：one_on_one  2：large_class  4：edu_medium_v1  6：proctoring]
     */
    @Deprecated("")
    var roomType = 0
}