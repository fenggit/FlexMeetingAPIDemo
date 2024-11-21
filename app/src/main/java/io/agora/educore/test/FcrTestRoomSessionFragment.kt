package io.agora.educore.test

import io.agora.agoracore.core2.bean.FcrRoomSession
import io.agora.agoracore.core2.bean.FcrRoomSessionParams
import io.agora.agoracore.core2.bean.FcrSessionResponse
import io.agora.agoracore.core2.control.observer.FcrRoomSessionObserver
import io.agora.core.common.log.LogX
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.test.tracer.Trace

/**
 * author : qinwei@agora.io
 * date : 2024/10/11 17:24
 * description :
 */
class FcrTestRoomSessionFragment : FcrTestBaseListFragment() {
    private var sessionId = ""
    private var params: FcrRoomSession? = null

    companion object {
        const val PAGE = "RoomSession"
    }

    private val observer = object : FcrRoomSessionObserver() {
        override fun onRoomSessionReceived(session: FcrRoomSession) {
            params = session
            LogX.d(PAGE, "$session")
        }

        override fun onRoomSessionAccepted(response: FcrSessionResponse) {
            super.onRoomSessionAccepted(response)
            LogX.d(PAGE, "$response")
        }

        override fun onRoomSessionRejected(response: FcrSessionResponse) {
            super.onRoomSessionRejected(response)
            LogX.d(PAGE, "$response")
        }
    }
    private val roomId: String
        get() {
            return arguments?.getString("roomId") ?: ""
        }
    override fun initData() {
        super.initData()
        val roomSession = mFcrTestUISceneViewModel.engine?.getRoomControl(roomId)?.getRoomSessionControl() ?: return
        roomSession.addObserver(observer)
        modules.add(ApiInfo("startRoomSession") {
            val payload = HashMap<String, Any>()
            payload["name"] = "hefeng"
            Trace.Builder(api).page(PAGE).info("call").commit()
            val params = FcrRoomSessionParams("test", 10, payload)
            sessionId = roomSession.startRoomSession(params, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("start room session success: $sessionId").commit()
                }

                override fun onFailure(error: FcrError) {
                    super.onFailure(error)
                    Trace.Builder(api).page(PAGE).info("start room session error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("stopRoomSession") {
            if (sessionId.isEmpty()) return@ApiInfo
            Trace.Builder(api).page(PAGE).info("success").commit()
            roomSession.stopRoomSession(sessionId)
        })
        modules.add(ApiInfo("acceptRoomSession") {
            params ?: return@ApiInfo
            val cause = HashMap<String, Any>()
            cause["phone1"] = "acceptRoomSession"
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomSession.acceptRoomSession(params!!, cause, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("accept room session success").commit()
                }

                override fun onFailure(error: FcrError) {
                    super.onFailure(error)
                    Trace.Builder(api).page(PAGE).info("accept room session error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("rejectRoomSession") {
            params ?: return@ApiInfo
            val cause = HashMap<String, Any>()
            cause["phone1"] = "rejectRoomSession"
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomSession.rejectRoomSession(params!!, cause, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("reject room session success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("accept room session error:$error").commit()
                }
            })
        })
    }

    override fun getPage(): String {
        return PAGE
    }

    override fun onDestroy() {
        super.onDestroy()
        mFcrTestUISceneViewModel.engine?.getRoomControl(roomId)?.getRoomSessionControl()?.removeObserver(observer)
    }
}