package io.agora.educore.test

import cn.shengwang.scene.FcrUIExitReason
import cn.shengwang.scene.FcrUISceneCreator
import io.agora.agoracore.core2.bean.FcrCloudRecordingAudioProfile
import io.agora.agoracore.core2.bean.FcrCloudRecordingConfig
import io.agora.agoracore.core2.bean.FcrLiveStreamingConfig
import io.agora.agoracore.core2.bean.FcrLiveStreamingLayoutType
import io.agora.agoracore.core2.bean.FcrLiveStreamingState
import io.agora.agoracore.core2.bean.FcrMessage
import io.agora.agoracore.core2.bean.FcrNetworkQualityEvent
import io.agora.agoracore.core2.bean.FcrNetworkStats
import io.agora.agoracore.core2.bean.FcrRecordingState
import io.agora.agoracore.core2.bean.FcrRoomControlCreateConfig
import io.agora.agoracore.core2.bean.FcrRoomJoinOptions
import io.agora.agoracore.core2.bean.FcrRoomPropertiesDeletedEvent
import io.agora.agoracore.core2.bean.FcrRoomPropertiesUpdatedEvent
import io.agora.agoracore.core2.bean.FcrRoomState
import io.agora.agoracore.core2.bean.FcrRoomType
import io.agora.agoracore.core2.bean.FcrStreamJoinConfig
import io.agora.agoracore.core2.bean.FcrStreamType
import io.agora.agoracore.core2.bean.FcrUserWaitingRoomMovedEvent
import io.agora.agoracore.core2.bean.FcrVideoEncoderConfig
import io.agora.agoracore.core2.bean.FcrVideoSize
import io.agora.agoracore.core2.control.api.FcrMainRoomControl
import io.agora.agoracore.core2.control.observer.FcrMainRoomObserver
import io.agora.core.common.log.LogX
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.test.tracer.Trace

/**
 * author : qinwei@agora.io
 * date : 2024/10/9 16:16
 * description :RoomControl api test
 */
class FcrTestRoomControlFragment : FcrTestBaseListFragment() {
    companion object {
        const val PAGE = "MainRoom"
    }

    private lateinit var roomControl: FcrMainRoomControl
    private lateinit var roomId: String
    private val mainRoomObserver = object : FcrMainRoomObserver {
        override fun onLocalUserMovedToWaitingRoom(roomId: String, event: FcrUserWaitingRoomMovedEvent) {
            Trace.Builder("onLocalUserMovedToWaitingRoom").page(PAGE).info("$event").commit()
            LogX.d(PAGE, "$roomId event:$event")
        }

        /**
         * 加入房间成功
         */
        override fun onJoinRoomSuccess(roomId: String) {
            Trace.Builder("onJoinRoomSuccess").page(PAGE).info(roomId).commit()
            LogX.d(PAGE, roomId)
        }

        /**
         * 加入房间失败
         */
        override fun onJoinRoomFailure(roomId: String, error: FcrError) {
            Trace.Builder("onJoinRoomFailure").page(PAGE).info("$roomId error:$error").commit()
            LogX.d(PAGE, "$roomId error:$error")
        }

        /**
         * 收到房间消息回调
         */
        override fun onRoomMessageReceived(roomId: String, message: FcrMessage) {
            Trace.Builder("onRoomMessageReceived").page(PAGE).info(roomId + message.toString()).commit()
            LogX.d(PAGE, "$roomId message:$message")
        }

        /**
         * 房间状态变更
         */
        override fun onRoomStateUpdated(roomId: String, state: FcrRoomState) {
            Trace.Builder("onRoomStateUpdated").page(PAGE).info("$roomId roomState:$state").commit()
            LogX.d(PAGE, "$roomId roomState:$state")
        }

        /**
         * 房间录制状态变更
         */
        override fun onCloudRecordingStateUpdated(roomId: String, state: FcrRecordingState) {
            Trace.Builder("onCloudRecordingStateUpdated").page(PAGE).info("$roomId recordingState:$state")
                .commit()
            LogX.d(PAGE, "$roomId recordingState:$state")
        }

        /**
         * 房间属性更新
         */
        override fun onRoomPropertiesUpdated(roomId: String, event: FcrRoomPropertiesUpdatedEvent) {
            Trace.Builder("onRoomPropertiesUpdated").page(PAGE).info("$roomId event:$event").commit()
            LogX.d(PAGE, "$roomId event:$event")
        }

        /**
         * 房间属性删除
         */
        override fun onRoomPropertiesDeleted(roomId: String, event: FcrRoomPropertiesDeletedEvent) {
            Trace.Builder("onRoomPropertiesDeleted").page(PAGE).info("$roomId event:$event").commit()
            LogX.d(PAGE, "$roomId event:$event")
        }

        /**
         * 本地媒体流上下行质量
         */
        override fun onNetworkQualityUpdated(roomId: String, event: FcrNetworkQualityEvent) {
//            Trace.Builder("onNetworkQualityUpdated").page(PAGE).info("$roomId event:$event").commit()
//            LogX.d(PAGE, "$roomId event:$event")
        }

        /**
         * 本端到边缘节点的网络质量统计
         */
        override fun onNetworkStatsUpdated(roomId: String, stats: FcrNetworkStats) {
//            Trace.Builder("onNetworkStatsUpdated").page(PAGE).info("$roomId stats:$stats").commit()
//            LogX.d(PAGE, "$roomId stats:$stats")
        }
        /**
         * 房间直播状态变更
         */
        override fun onLiveStreamingStateUpdated(roomId: String, state: FcrLiveStreamingState, url: String) {
            Trace.Builder("onLiveStreamingStateUpdated").page(PAGE).info("$roomId state:$state url:$url").commit()
        }
    }

    override fun initData() {
        super.initData()
        roomId = arguments?.getString("roomId") ?: ""
        roomControl = mFcrTestUISceneViewModel.engine?.getRoomControl(roomId) as? FcrMainRoomControl ?: return
        roomControl.addObserver(mainRoomObserver)

        modules.add(ApiInfo("getPrivilegeControl") {
            Trace.Builder(api).page(PAGE).info("${roomControl.getPrivilegeControl()}").commit()
        })
        modules.add(ApiInfo("getUserControl") {
            Trace.Builder(api).page(PAGE).info("${roomControl.getUserControl()}").commit()
        })
        modules.add(ApiInfo("getStreamControl") {
            Trace.Builder(api).page(PAGE).info("${roomControl.getStreamControl()}").commit()
        })
//        modules.add(ApiInfo("getWidgetControl") {
//            Trace.Builder(api).page(PAGE).info("${roomControl.getWidgetControl()}").commit()
//        })
        modules.add(ApiInfo("getGroupControl") {
            Trace.Builder(api).page(PAGE).info("${roomControl.getGroupControl()}").commit()
        })
        modules.add(ApiInfo("getRoomSessionControl") {
            Trace.Builder(api).page(PAGE).info("${roomControl.getRoomSessionControl()}").commit()
        })
        modules.add(ApiInfo("getChatRoomControl") {
            Trace.Builder(api).page(PAGE).info("${roomControl.getChatRoomControl()}").commit()
        })
        modules.add(ApiInfo("getBoardControl") {
            Trace.Builder(api).page(PAGE).info("${roomControl.getBoardControl()}").commit()
        })
        modules.add(ApiInfo("join") {
            val config = mFcrTestUISceneViewModel.config ?: return@ApiInfo
            Trace.Builder(api).page(PAGE).info("call").commit()
            val videoSourceId = engine.getMobileMediaControl().getCameraInfo().deviceId
            val audioSourceId = engine.getMobileMediaControl().getMicInfo().deviceId
            val createStreamConfigs = ArrayList<FcrStreamJoinConfig>()
            val streamInfo = FcrStreamJoinConfig(null, videoSourceId, audioSourceId, "Tom Stream", FcrStreamType.BOTH)
            createStreamConfigs.add(streamInfo)
            val option = FcrRoomJoinOptions(
                config.userName,
                config.userRole,
                config.userProperties,
                config.token,
                config.streamLatency,
                createStreamConfigs
            )
            val sceneConfig = mFcrTestUISceneViewModel.config ?: return@ApiInfo
            val roomConfig = FcrRoomControlCreateConfig(sceneConfig.roomId, FcrRoomType.MAIN_ROOM)
            roomControl = (engine.createRoomControl(roomConfig) as FcrMainRoomControl?)!!
            roomControl.join(option, object : FcrCallback<Unit> {
                override fun onSuccess(res: Unit?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })

        modules.add(ApiInfo("leave") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.leave()
            FcrUISceneCreator.getObserverOfUIScene()[roomId]?.onExited(roomId, FcrUIExitReason.LEAVEROOM)
        })

        modules.add(ApiInfo("start") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.start(object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("end") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.end(object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("close") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.close(object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })

        modules.add(ApiInfo("getSyncTimestamp") {
            Trace.Builder(api).page(PAGE).info(roomControl.getSyncTimestamp().toString()).commit()
        })

        modules.add(ApiInfo("getRoomInfo") {
            val room = roomControl.getRoomInfo()
            Trace.Builder(api).page(PAGE).info("${room.roomId} - ${room.roomName}").commit()
        })

        modules.add(ApiInfo("getRoomSchedule") {
            Trace.Builder(api).page(PAGE).info("${roomControl.getRoomSchedule()}").commit()
        })

        modules.add(ApiInfo("getRoomState") {
            val state = roomControl.getRoomState()
            Trace.Builder(api).page(PAGE).info(state.toString()).commit()
        })
        modules.add(ApiInfo("getRoomProperties") {
            val properties = roomControl.getRoomProperties()
            Trace.Builder(api).page(PAGE).info(properties.toString()).commit()
        })
        modules.add(ApiInfo("getRoomPropertiesByKeyPath") {
            val properties = roomControl.getRoomPropertiesByKeyPath("")
            Trace.Builder(api).page(PAGE).info(properties.toString()).commit()
        })
        modules.add(ApiInfo("updateRoomProperties") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val properties = HashMap<String, Any>()
            properties["roomName"] = "FcrcoreTestRoom"
            roomControl.updateRoomProperties(roomProperties = properties, callback = object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("deleteRoomProperties") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val properties = HashMap<String, Any>()
            properties["roomName"] = "FcrcoreTestRoom"
            roomControl.deleteRoomProperties(arrayListOf("roomName"), properties, callback = object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("updateIncrementRoomProperties") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val properties = HashMap<String, Int>()
            properties["zan"] = 1
            roomControl.updateIncrementRoomProperties(increments = properties, callback = object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("startCloudRecording") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val recording = FcrCloudRecordingConfig()
            val videoEncoderConfig = FcrVideoEncoderConfig()
            videoEncoderConfig.dimensions = FcrVideoSize(1280, 720)
            videoEncoderConfig.bitRate = 1200
            videoEncoderConfig.frameRate = 15
            recording.videoEncoderConfig = videoEncoderConfig
            recording.audioProfile = FcrCloudRecordingAudioProfile.musicmono48k
            roomControl.startCloudRecording(recording, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("pauseCloudRecording") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.pauseCloudRecording(object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("resumeCloudRecording") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.resumeCloudRecording(object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("stopCloudRecording") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.stopCloudRecording(object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("getCloudRecordingState") {
            Trace.Builder(api).page(PAGE).info(roomControl.getCloudRecordingState().toString()).commit()
        })
        modules.add(ApiInfo("getLiveStreamingConfig") {
            Trace.Builder(api).page(PAGE).info(roomControl.getLiveStreamingConfig().toString()).commit()
        })
        modules.add(ApiInfo("startLiveStreaming") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val info = FcrLiveStreamingConfig(
                "abvgt3",
                "rtmp://pub.myun.tv/watch/",
                "https://webcasting-test.bizconf.cn/live/watch/o1r133pm",
                FcrLiveStreamingLayoutType.SPEAKER
            )
            roomControl.startLiveStreaming(info, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("stopLiveStreaming") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.stopLiveStreaming(object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("updateLiveStreamingLayout") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.updateLiveStreamingLayout(FcrLiveStreamingLayoutType.SPEAKER, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo(" getLiveStreamingState") {
            Trace.Builder(api).page(PAGE).info(roomControl.getLiveStreamingState().toString()).commit()
        })
        modules.add(ApiInfo("sendRoomMessage") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val data = HashMap<String, Any>()
            data["SendRoomMessage1"] = "hello"
            data["SendRoomMessage2"] = "world!!!"
            roomControl.sendRoomMessage(data, callback = object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("success").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        adapter.notifyDataSetChanged()
    }

    override fun getPage(): String {
        return PAGE
    }

    override fun onDestroy() {
        super.onDestroy()
        roomControl.removeObserver(mainRoomObserver)
    }
}