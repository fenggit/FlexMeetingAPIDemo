package io.agora.educore.test

import io.agora.agoracore.core2.bean.FcrTargetUserType
import io.agora.agoracore.core2.control.privilege.FcrPermissionInfoAddedEvent
import io.agora.agoracore.core2.control.privilege.FcrPermissionInfoDeletedEvent
import io.agora.agoracore.core2.control.privilege.FcrPrivilegeObserver
import io.agora.agoracore.core2.control.privilege.FcrSecurityInfoUpdatedEvent
import io.agora.core.common.http.bean.HttpBaseRes
import io.agora.core.common.log.LogX
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.test.tracer.Trace

/**
 * author : chenbinhang@agora.io
 * date : 2024/10/14
 * description :
 */
class FcrTestPrivilegeFragment : FcrTestBaseListFragment() {
    companion object {
        const val PAGE = "Privilege"
    }

    private val observer = object : FcrPrivilegeObserver {
        /**
         * on security info updated
         * @param roomId: the room id
         * @param event: the security info updated event
         */
        override fun onSecurityInfoUpdated(roomId: String, event: FcrSecurityInfoUpdatedEvent) {
            LogX.i("onSecurityInfoUpdated", "roomId: $roomId, event: $event")
        }

        /**
         * on user permission info added
         * @param roomId: the room id
         * @param event: the user permission info added event
         */
        override fun onLocalUserPermissionInfoAdded(roomId: String, event: FcrPermissionInfoAddedEvent) {
            LogX.i("onLocalUserPermissionInfoAdded", "roomId: $roomId, event: $event")
        }

        /**
         * on user permission info deleted
         * @param roomId: the room id
         * @param event: the user permission info deleted event
         */
        override fun onLocalUserPermissionInfoDeleted(roomId: String, event: FcrPermissionInfoDeletedEvent) {
            LogX.i("onLocalUserPermissionInfoDeleted", "roomId: $roomId, event: $event")
        }
    }
    private val roomId: String
        get() {
            return arguments?.getString("roomId") ?: ""
        }
    override fun initData() {
        super.initData()
        engine.getRoomControl(roomId)?.getPrivilegeControl()?.addObserver(observer)

        modules.add(ApiInfo("getSecurityInfo") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val securityInfos = engine.getRoomControl(roomId)?.getPrivilegeControl()?.getAllSecurityInfo()
            securityInfos?.forEach {
                val securityInfo = engine.getRoomControl(roomId)?.getPrivilegeControl()?.getSecurityInfo(it.info.action)
                Trace.Builder(api).page(PAGE).info("getPrivilegeControl ${it.info.action.key}:${securityInfo?.enable}").commit()
            }
        })

        modules.add(ApiInfo("getSecurityInfo") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val securityInfos = engine.getRoomControl(roomId)?.getPrivilegeControl()?.getAllSecurityInfo()
            securityInfos?.forEach {
                Trace.Builder(api).page(PAGE).info("getPrivilegeControl securityInfo:$it").commit()
            }
        })


        modules.add(ApiInfo("getLocalUserPermissionInfo") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val permissionInfos = engine.getRoomControl(roomId)?.getPrivilegeControl()?.getAllLocalUserPermissionInfo()
            permissionInfos?.forEach {
                it.info?.action?.let { it1 ->
                    val permissionInfo = engine.getRoomControl(roomId)?.getPrivilegeControl()?.getLocalUserPermissionInfo(it1)
                    Trace.Builder(api).page(PAGE).info("getPrivilegeControl ${permissionInfo!!.info!!.action}:${permissionInfo.enable}").commit()
                }
            }
        })

        modules.add(ApiInfo("getAllLocalUserPermissionInfo") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val permissionInfos = engine.getRoomControl(roomId)?.getPrivilegeControl()?.getAllLocalUserPermissionInfo()
            Trace.Builder(api).page(PAGE).info("getPrivilegeControl securityInfo:$permissionInfos").commit()
        })

        modules.add(ApiInfo("enableLockedRoom") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.enableLockedRoom(true,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl enableLockedRoom  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl enableLockedRoom  Callback: false").commit()
                    }
                }
            )
        })

        modules.add(ApiInfo("disEnableLockedRoom") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.enableLockedRoom(false,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl unLockedRoom  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl unLockedRoom  Callback: false").commit()
                    }
                }
            )
        })

        //allowShareScreen
        modules.add(ApiInfo("allowShareScreen") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowShare(true,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowShareScreen  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowShareScreen  Callback: false").commit()
                    }
                }
            )
        })

        modules.add(ApiInfo("disAllowShareScreen ") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowShare(false,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disallowShareScreen  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disallowShareScreen  Callback: false").commit()
                    }
                }
            )
        })

        //
        modules.add(ApiInfo("allowShare") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowShare(true,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowShare  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowShare  Callback: false").commit()
                    }
                }
            )
        })

        modules.add(ApiInfo("disallowShare") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowShare(false,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disallowShare  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disallowShare  Callback: false").commit()
                    }
                }
            )
        })
//

        modules.add(ApiInfo("allowWriteBoard") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowWriteBoard(true,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowWriteBoard  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowWriteBoard  Callback: false").commit()
                    }
                }
            )
        })

        modules.add(ApiInfo("disAllowWriteBoard") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowWriteBoard(false,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disallowWriteBoard  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disallowWriteBoard  Callback: false").commit()
                    }
                }
            )
        })

        //allowSendChat
        modules.add(ApiInfo("allowSendChat") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowSendChat(true,
                arrayOf(FcrTargetUserType.PARTICIPANT), null,
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowSendChat  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowSendChat  Callback: false").commit()
                    }
                }
            )
        })

        modules.add(ApiInfo("disAllowSendChat") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowSendChat(false,
                arrayOf(FcrTargetUserType.PARTICIPANT),null,
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disAllowSendChat  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disAllowSendChat  Callback: false").commit()
                    }
                }
            )
        })

        //allowStartAudio
        modules.add(ApiInfo("allowStartAudio") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowStartAudio(true,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowStartAudio  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowStartAudio  Callback: false").commit()
                    }
                }
            )
        })

        modules.add(ApiInfo("disAllowStartAudio") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowStartAudio(false,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disallowStartAudio  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disallowStartAudio  Callback: false").commit()
                    }
                }
            )
        })

        //allowStartVideo
        modules.add(ApiInfo("allowStartVideo") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowStartVideo(true,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowStartVideo  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowStartVideo  Callback: false").commit()
                    }
                }
            )
        })

        modules.add(ApiInfo("disAllowStartVideo") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowStartVideo(false,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disAllowStartVideo  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disAllowStartVideo  Callback: false").commit()
                    }
                }
            )
        })
        //allowJoinWithMuteAudio

        modules.add(ApiInfo("allowJoinWithMuteAudio") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowJoinWithMutedAudio(true,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowJoinWithMuteAudio  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl allowJoinWithMuteAudio  Callback: false").commit()
                    }
                }
            )
        })

        modules.add(ApiInfo("disAllowJoinWithMuteAudio") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getPrivilegeControl()?.allowJoinWithMutedAudio(false,
                arrayOf(FcrTargetUserType.PARTICIPANT),
                object : FcrCallback<HttpBaseRes<Any>?> {
                    override fun onSuccess(res: HttpBaseRes<Any>?) {
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disallowJoinWithMuteAudio  Callback: true").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("getPrivilegeControl disallowJoinWithMuteAudio  Callback: false").commit()
                    }
                }
            )
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        engine.getRoomControl(roomId)?.getPrivilegeControl()?.removeObserver(observer)
    }
}