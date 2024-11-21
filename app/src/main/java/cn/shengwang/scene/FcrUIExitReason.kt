package cn.shengwang.scene

/**
 * author : felix
 * date : 2024/1/23
 * description :
 */
enum class FcrUIExitReason {
    /**
     * 自己离开而退出
     */
    LEAVEROOM,

    /**
     * 房间关闭而退出
     */
    CLOSEROOM,

    /**
     * 被踢出房间而退出
     */
    KICKOUT
}