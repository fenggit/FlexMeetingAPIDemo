package io.agora.educore.test

import io.agora.agoracore.core2.bean.FcrIPConnectorSessionParams
import io.agora.agoracore.core2.bean.FcrIPConnectorSessionType
import io.agora.agoracore.core2.bean.FcrPhoneConnectorSessionParams
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.test.tracer.Trace

/**
 * author : qinwei@agora.io
 * date : 2024/10/17 14:30
 * description :
 */
class FcrTestRoomConnectorControlFragment : FcrTestBaseListFragment() {
    var callId: String? = ""
    private val roomId: String
        get() {
            return arguments?.getString("roomId") ?: ""
        }
    override fun initData() {
        super.initData()
        //getRoomConnectorPhoneInfo
        val roomConnectorControl = engine.getRoomControl(roomId)?.getRoomConnectorControl() ?: return
        modules.add(ApiInfo("getRoomConnectorPhoneInfo") {
            Trace.Builder(api).info(roomConnectorControl.getRoomConnectorPhoneInfo().toString()).commit()
        })
        modules.add(ApiInfo("getRoomConnectorIpInfo") {
            Trace.Builder(api).info(roomConnectorControl.getRoomConnectorIpInfo().toString()).commit()
        })
        modules.add(ApiInfo("startSessionByPhone") {
            val p = FcrPhoneConnectorSessionParams("18221125330", "hefeng")
            Trace.Builder(api).info("call").commit()
            roomConnectorControl.startSessionByPhone(p, object : FcrCallback<String> {
                override fun onSuccess(res: String?) {
                    callId = res
                    Trace.Builder(api).info("onSuccess $res").commit()
                }

                override fun onFailure(error: FcrError) {
                    super.onFailure(error)
                    Trace.Builder(api).info("onFailure $error").commit()
                }
            })
        })
        modules.add(ApiInfo("startSessionByIP") {
            val p = FcrIPConnectorSessionParams("1003", FcrIPConnectorSessionType.SIP)
            Trace.Builder(api).info("call").commit()
            roomConnectorControl.startSessionByIP(p, object : FcrCallback<String> {
                override fun onSuccess(res: String?) {
                    callId = res
                    Trace.Builder(api).info("onSuccess $res").commit()
                }

                override fun onFailure(error: FcrError) {
                    super.onFailure(error)
                    Trace.Builder(api).info("onFailure $error").commit()
                }
            })
        })
        modules.add(ApiInfo("stopSession") {
            val p = FcrIPConnectorSessionParams("1003", FcrIPConnectorSessionType.SIP)
            roomConnectorControl.stopSession(callId ?: "", object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).info("onSuccess").commit()
                }

                override fun onFailure(error: FcrError) {
                    super.onFailure(error)
                    Trace.Builder(api).info("onFailure $error").commit()
                }
            })
        })
    }
}