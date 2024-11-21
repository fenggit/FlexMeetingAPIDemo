package cn.shengwang.http

import cn.shengwang.http.bean.FcrSceneCreateRoomReq
import cn.shengwang.http.bean.FcrSceneCreateRoomRes
import cn.shengwang.http.bean.FcrSceneJoinRoomReq
import cn.shengwang.http.bean.FcrSceneJoinRoomRes
import io.agora.core.common.http.bean.HttpBaseRes
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * author : felix
 * date : 2024/4/15
 * description :
 *
 *   1. 创建房间
 *   https://yapi.sh2.agoralab.co/project/53/interface/api/1929
 *   2. 加入房间
 *   https://yapi.sh2.agoralab.co/project/53/interface/api/1939
 *   3. 房间列表
 *   https://yapi.sh2.agoralab.co/project/53/interface/api/1924
 *   4.预检
 *   https://yapi.sh2.agoralab.co/project/53/interface/api/1974
 */
interface FcrSceneRoomService {
    /**
     * 创建房间(免登录)
     *
     * createRoomWithLogin
     */
    @POST("conference/companys/v1/rooms")
    fun createRoom(@Body req: FcrSceneCreateRoomReq): Call<HttpBaseRes<FcrSceneCreateRoomRes>>

    /**
     * 加入房间(免登录)
     */
    @PUT("conference/companys/v1/rooms")
    fun joinRoom(@Body req: FcrSceneJoinRoomReq): Call<HttpBaseRes<FcrSceneJoinRoomRes>>

}