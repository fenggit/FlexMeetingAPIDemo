package io.agora.educore.test

import io.agora.agoracore.core2.FcrCoreEngineObserver
import io.agora.agoracore.core2.bean.FcrConnectionState
import io.agora.agoracore.core2.bean.FcrMessage
import io.agora.core.common.log.LogX
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.test.tracer.Trace


/**
 * author : qinwei@agora.io
 * date : 2024/10/9 16:16
 * description :
 */
class FcrTestCoreEngineFragment : FcrTestBaseListFragment() {
    companion object {
        const val PAGE = "CoreEngine"
    }

    private val observer = object : FcrCoreEngineObserver {
        override fun onConnectionStateUpdated(state: FcrConnectionState) {
//            Trace.Builder("onConnectionStateUpdated").page(PAGE).info(state.toString()).commit()
            LogX.d(PAGE, "$state")
        }

        override fun onPeerMessageReceived(message: FcrMessage) {
//            Trace.Builder("onPeerMessageReceived").page(PAGE).info(message.toString()).commit()
            LogX.d(PAGE, "$message")
        }
    }
    private val roomId: String
        get() {
            return arguments?.getString("roomId") ?: ""
        }

    override fun initData() {
        super.initData()
        val engine = mFcrTestUISceneViewModel.engine ?: return
        engine.addObserver(observer)
        modules.add(ApiInfo("getVersion") {
            Trace.Builder(api).page(PAGE).info(engine.getVersion()).commit()
        })
        modules.add(ApiInfo("login") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.login(object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("onSuccess").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })

        modules.add(ApiInfo("logout") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.logout(object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("onSuccess").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })

        modules.add(ApiInfo("createRoomControlAndJoin") {
            Trace.Builder(api).page(PAGE).info("call").commit()
        })
        modules.add(ApiInfo("createRoomControl") {
            Trace.Builder(api).page(PAGE).info("call").commit()
        })
        modules.add(ApiInfo("getMediaControl") {
            Trace.Builder(api).page(PAGE).info(engine.getMobileMediaControl().toString()).commit()
        })
        modules.add(ApiInfo("getMonitorControl") {
            Trace.Builder(api).page(PAGE).info(engine.getMonitorControl().toString()).commit()
        })
        modules.add(ApiInfo("getPeerSessionControl") {
            Trace.Builder(api).page(PAGE).info(engine.getPeerSessionControl().toString()).commit()
        })

        modules.add(ApiInfo("sendPeerMessage") {
            val m = HashMap<String, Any>()
            m["name"] = "hefeng"
            Trace.Builder(api).page(PAGE).info("call").commit()
            val userId = engine.getRoomControl(roomId)?.getUserControl()?.getLocalUser()?.userId ?: ""
            engine.sendPeerMessage(m,false, userId, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("onSuccess").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })

        modules.add(ApiInfo("setParameters") {
            Trace.Builder(api).page(PAGE).info("call").commit()
//            engine.setParameters(FcrLunchConfigParameters.getLunchConfigParameters())
        })
    }

    override fun getPage(): String {
        return PAGE
    }

    override fun onDestroy() {
        super.onDestroy()
        engine.removeObserver(observer)
    }
}