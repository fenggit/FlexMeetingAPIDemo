package io.agora.educore.test

import io.agora.agoracore.core2.bean.FcrPeerSession
import io.agora.agoracore.core2.bean.FcrPeerSessionParams
import io.agora.agoracore.core2.bean.FcrRoomSession
import io.agora.agoracore.core2.bean.FcrSessionResponse
import io.agora.agoracore.core2.control.observer.FcrPeerSessionObserver
import io.agora.core.common.log.LogX
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.test.tracer.Trace

/**
 * author : qinwei@agora.io
 * date : 2024/10/11 14:19
 * description :
 */
class FcrTestPeerSessionFragment : FcrTestBaseListFragment() {
    companion object {
        const val PAGE = "PeerSession"
    }

    private var sessionId = ""

    private var params: FcrRoomSession? = null
    private var peerSessionObserver = object : FcrPeerSessionObserver() {
        override fun onPeerSessionReceived(session: FcrPeerSession) {
            super.onPeerSessionReceived(session)
            params = session
            LogX.d(PAGE, "onPeerSessionReceived session=$session")
        }

        override fun onPeerSessionAccepted(response: FcrSessionResponse) {
            LogX.d(PAGE, "onPeerSessionAccepted response=$response")
        }

        override fun onPeerSessionRejected(response: FcrSessionResponse) {
            LogX.d(PAGE, "onPeerSessionRejected response=$response")
        }
    }
    private val roomId: String
        get() {
            return arguments?.getString("roomId") ?: ""
        }
    override fun initData() {
        super.initData()
        val engine = mFcrTestUISceneViewModel.engine ?: return
        val peerSessionControl = engine.getPeerSessionControl() ?: return
        peerSessionControl.addObserver(peerSessionObserver)
        modules.add(ApiInfo("startPeerSession") {
            val payload = HashMap<String, Any>()
            payload["name"] = "hefeng"
            val receiverId = engine.getRoomControl(roomId)?.getUserControl()?.getLocalUser()?.userId ?: ""
            val params = FcrPeerSessionParams("test", receiverId, 10, payload)
            Trace.Builder(api).page(PAGE).info("call").commit()
            sessionId = peerSessionControl.startPeerSession(params, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("start room session success").commit()
                }

                override fun onFailure(error: FcrError) {
                    super.onFailure(error)
                    Trace.Builder(api).page(PAGE).info("start room session error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("stopPeerSession") {
            if (sessionId.isEmpty()) {
                return@ApiInfo
            }
            Trace.Builder(api).page(PAGE).info("stopPeerSession success").commit()
            peerSessionControl.stopPeerSession(sessionId)
        })
        modules.add(ApiInfo("acceptPeerSession") {
            if (params == null) return@ApiInfo
            val cause = HashMap<String, Any>()
            cause["phone1"] = "acceptPeerSession"
            Trace.Builder(api).page(PAGE).info("call").commit()
            peerSessionControl.acceptPeerSession(params!!, cause, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("accept peer session success").commit()
                }

                override fun onFailure(error: FcrError) {
                    super.onFailure(error)
                    Trace.Builder(api).page(PAGE).info("accept peer session error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("rejectPeerSession") {
            if (params == null) return@ApiInfo
            val cause = HashMap<String, Any>()
            cause["phone1"] = "rejectPeerSession"
            Trace.Builder(api).page(PAGE).info("call").commit()
            peerSessionControl.rejectPeerSession(params!!, cause, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("reject peer session success").commit()
                }

                override fun onFailure(error: FcrError) {
                    super.onFailure(error)
                    Trace.Builder(api).page(PAGE).info("reject peer session error:$error").commit()
                }
            })
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mFcrTestUISceneViewModel.engine?.getPeerSessionControl()?.removeObserver(peerSessionObserver)
    }

    override fun getPage(): String {
        return PAGE
    }

}