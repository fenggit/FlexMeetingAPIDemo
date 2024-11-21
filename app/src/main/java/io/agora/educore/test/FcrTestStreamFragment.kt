package io.agora.educore.test

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import io.agora.agoracore.core2.FcrCoreEngine
import io.agora.agoracore.core2.bean.FcrLocalAudioStats
import io.agora.agoracore.core2.bean.FcrLocalStreamCreateConfig
import io.agora.agoracore.core2.bean.FcrLocalVideoStats
import io.agora.agoracore.core2.bean.FcrRemoteAudioStats
import io.agora.agoracore.core2.bean.FcrRemoteVideoStats
import io.agora.agoracore.core2.bean.FcrSnapshotInfo
import io.agora.agoracore.core2.bean.FcrStreamEvent
import io.agora.agoracore.core2.bean.FcrStreamInfo
import io.agora.agoracore.core2.bean.FcrStreamType
import io.agora.agoracore.core2.bean.FcrTargetUserType
import io.agora.agoracore.core2.bean.FcrUpdateStreamPrivilege
import io.agora.agoracore.core2.bean.FcrUpdateStreamPrivilegeScope
import io.agora.agoracore.core2.bean.FcrVideoEncoderConfig
import io.agora.agoracore.core2.bean.FcrVideoSize
import io.agora.agoracore.core2.bean.FcrVideoStreamType
import io.agora.agoracore.core2.control.api.FcrMainRoomControl
import io.agora.agoracore.core2.control.observer.FcrStreamObserver
import io.agora.agoracore.rte2.pub.bean.AgoraRteStreamPrivilegeOperation
import io.agora.core.common.log.LogX
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.test.tracer.Trace
import io.agora.educore.test.view.ListFragmentDialog

/**
 * author : chenbinhang@agora.io
 * date : 2024/10/14
 * description :
 */
class FcrTestStreamFragment : FcrTestBaseListFragment() {
    companion object {
        const val PAGE = "Stream"
        fun selectStream(
            roomId: String,
            engine: FcrCoreEngine,
            childFragmentManager: FragmentManager,
            tag: String,
            action: (stream: FcrStreamInfo) -> Unit
        ) {
            engine.getRoomControl(roomId)?.getStreamControl()?.getStreamList()?.let { streams ->
                val list = streams.map { it.streamId }.toTypedArray()
                val dialog = ListFragmentDialog()
                val args = Bundle()
                args.putStringArray(ListFragmentDialog.ITEMS_KEY, list)
                dialog.arguments = args
                dialog.setOnItemSelectedListener { index, _ ->
                    val stream = streams[index]
                    action(stream)
                }.show(childFragmentManager, tag)
            }
        }
    }


    private lateinit var roomControl: FcrMainRoomControl
    private lateinit var roomId: String
    private val observer = object : FcrStreamObserver {
        /**
         * 新增的流
         */
        override fun onStreamsAdded(roomId: String, event: List<FcrStreamEvent>) {
            LogX.i("onStreamsAdded roomId:$roomId event:$event")
        }

        /**
         * 流移除
         */
        override fun onStreamsRemoved(roomId: String, event: List<FcrStreamEvent>) {
            event.stream()
                .forEach {
                    LogX.i("onStreamsRemoved roomId:$roomId event:$it")
                }
        }

        /**
         * 流信息更新
         */
        override fun onStreamsUpdated(roomId: String, event: List<FcrStreamEvent>) {
            event.stream()
                .forEach {
                    LogX.i("onStreamsUpdated roomId:$roomId event:$it")
                }
        }

        /**
         * 远端首帧视频渲染完成
         * @param roomId 房间id
         * @param streamId 流id
         * @param size 视频宽高
         */
        override fun onFirstRemoteVideoFrameRendered(roomId: String, streamId: String, size: FcrVideoSize) {
            LogX.i("onFirstRemoteVideoFrameRendered roomId:$roomId streamId:$streamId size:$size")
        }

        /**
         * 本地首帧视频渲染完成
         * @param roomId 房间id
         * @param streamId 流id
         * @param videoStats 本地视频统计数据更新
         */
        override fun onLocalVideoStatsUpdated(roomId: String, streamId: String, videoStats: FcrLocalVideoStats) {
            LogX.i("onLocalVideoStatsUpdated roomId:$roomId streamId:$streamId videoStats:$videoStats")
        }

        /**
         * 本地音频统计数据
         * @param roomId 房间id
         * @param streamId 流id
         * @param audioStats 本地音频统计数据
         */
        override fun onLocalAudioStatsUpdated(roomId: String, streamId: String, audioStats: FcrLocalAudioStats) {
            LogX.i("onLocalAudioStatsUpdated roomId:$roomId streamId:$streamId audioStats:$audioStats")
        }

        /**
         * 远端视频统计数据
         * @param roomId 房间id
         * @param streamId 流id
         * @param videoStats 远端视频统计数据
         */
        override fun onRemoteVideoStatsUpdated(roomId: String, streamId: String, videoStats: FcrRemoteVideoStats) {
            LogX.i("onRemoteVideoStatsUpdated roomId:$roomId streamId:$streamId videoStats:$videoStats")
        }

        /**
         * 远端音频统计数据
         * @param roomId 房间id
         * @param streamId 流id
         * @param audioStats 远端音频统计数据
         */
        override fun onRemoteAudioStatsUpdated(roomId: String, streamId: String, audioStats: FcrRemoteAudioStats) {
            LogX.i("onRemoteAudioStatsUpdated roomId:$roomId streamId:$streamId audioStats:$audioStats")
        }
    }

    override fun initData() {
        super.initData()
        roomId = arguments?.getString("roomId") ?: ""
        roomControl = engine.getRoomControl(roomId) as? FcrMainRoomControl ?: return
        roomControl.getStreamControl()?.addObserver(observer)
        modules.add(ApiInfo("getStreams") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.getStreamControl()?.getStreams()?.let {
                it.entries.forEach {
                    Trace.Builder(api).page(PAGE).info("getStreams streamId:${it.key} streamInfo:${it.value}").commit()
                }
            }
        })

        modules.add(ApiInfo("getStreamList") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.getStreamControl()?.getStreamList()?.let {
                it.forEach {
                    Trace.Builder(api).page(PAGE).info("getStreams streamId:${it.streamId} streamInfo:${it.streamName}")
                        .commit()
                }
            }
        })

        modules.add(ApiInfo("getStreamList") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.getStreamControl()?.getStreamList()?.let {
                it.forEach {
                    Trace.Builder(api).page(PAGE).info("getStreams streamId:${it.streamId} streamInfo:${it.streamName}")
                        .commit()
                }
            }
        })

        modules.add(ApiInfo("getStreamsByUserId") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.getUserControl()?.getUserList()?.forEach { user ->
                roomControl.getStreamControl()?.getStreamsByUserId(user.userId)?.let { array ->
                    array.forEach { stream ->
                        Trace.Builder(api).page(PAGE)
                            .info("getStreamsByUserId userId:${user.userId} streamId:${stream.streamId} streamInfo:${stream}")
                            .commit()
                    }

                }
            }
        })

        modules.add(ApiInfo("getStreamByStreamId") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            roomControl.getStreamControl()?.getStreamList()?.forEach {
                roomControl.getStreamControl()?.getStreamByStreamId(it.streamId)?.let { stream ->
                    Trace.Builder(api).page(PAGE)
                        .info("getStreamByStreamId streamId:${stream.streamId} streamInfo:${stream.streamName}")
                        .commit()
                }
            }
        })

        modules.add(ApiInfo("addLocalStreams") {
            val list = ArrayList<FcrLocalStreamCreateConfig>()
            list.add(FcrLocalStreamCreateConfig("myStream", FcrStreamType.BOTH))
            roomControl.getStreamControl()?.addLocalStreams(list, null)
        })


        modules.add(ApiInfo("updatePublishPrivilegeOfStreams") {
            selectStream(roomId, engine, childFragmentManager, "updatePublishPrivilegeOfStreams") { stream ->
                val map = HashMap<String, FcrUpdateStreamPrivilege>()
                map[stream.streamId] =
                    FcrUpdateStreamPrivilege(
                        AgoraRteStreamPrivilegeOperation.HAS_PRIVILEGE,
                        AgoraRteStreamPrivilegeOperation.HAS_PRIVILEGE
                    )
                roomControl.getStreamControl()
                    ?.updatePublishPrivilegeOfStreams(map, object : FcrCallback<Any> {
                        override fun onSuccess(res: Any?) {
                            Trace.Builder(api).page(PAGE).info("updatePublishPrivilegeOfStreams success").commit()
                        }

                        override fun onFailure(error: FcrError) {
                            super.onFailure(error)
                            Trace.Builder(api).page(FcrTestUserFragment.PAGE).info("fetchUserList error:${error}")
                                .commit()
                        }
                    })
            }
        })

        modules.add(ApiInfo("updatePublishPrivilegeOfStreams") {
            selectStream(roomId, engine, childFragmentManager, "updatePublishPrivilegeOfStreams") { stream ->
                val privilege =
                    FcrUpdateStreamPrivilege(
                        AgoraRteStreamPrivilegeOperation.HAS_PRIVILEGE,
                        AgoraRteStreamPrivilegeOperation.HAS_PRIVILEGE
                    )
                val scope = FcrUpdateStreamPrivilegeScope(
                    targetRoleType = FcrTargetUserType.PARTICIPANT.index,
                    targetRoleIsInclude = true
                )
                roomControl.getStreamControl()
                    ?.updatePublishPrivilegeOfStreams(privilege, scope, object : FcrCallback<Any> {
                        override fun onSuccess(res: Any?) {
                            Trace.Builder(api).page(PAGE).info("updatePublishPrivilegeOfStreams success").commit()
                        }

                        override fun onFailure(error: FcrError) {
                            super.onFailure(error)
                            Trace.Builder(api).page(FcrTestUserFragment.PAGE)
                                .info("updatePublishPrivilegeOfStreams error:${error}").commit()
                        }
                    })
            }
        })

        modules.add(ApiInfo("removeStreams") {
            val streams = ArrayList<String>()
            roomControl.getStreamControl()?.removeStreams(streams, object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("removeStreams success streams:${streams}").commit()
                }

                override fun onFailure(error: FcrError) {
                    super.onFailure(error)
                    Trace.Builder(api).page(FcrTestUserFragment.PAGE)
                        .info("fetchUserList error:${error} streams:${streams}").commit()
                }
            })
        })

        modules.add(ApiInfo("setVideoEncoderConfig") {
            selectStream(roomId, engine, childFragmentManager, "setVideoEncoderConfig") { stream ->
                val config = FcrVideoEncoderConfig()
                val streamId = stream.streamId
                val type = FcrVideoStreamType.LOW
                roomControl.getStreamControl()?.setVideoEncoderConfig(streamId, config, type)
            }

        })

        modules.add(ApiInfo("stopRenderRemoteVideoStream") {
            selectStream(roomId, engine, childFragmentManager, "stopRenderRemoteVideoStream") { stream ->
                val streamId = stream.streamId
                roomControl.getStreamControl()?.stopRenderRemoteVideoStream(streamId)
            }
        })

        modules.add(ApiInfo("startPlayRemoteAudioStream") {
            selectStream(roomId, engine, childFragmentManager, "startPlayRemoteAudioStream") { stream ->
                val streamId = stream.streamId
                roomControl.getStreamControl()?.startPlayRemoteAudioStream(streamId)
            }
        })

        modules.add(ApiInfo("stopPlayRemoteAudioStream") {
            selectStream(roomId, engine, childFragmentManager, "stopPlayRemoteAudioStream") { stream ->
                val streamId = stream.streamId
                roomControl.getStreamControl()?.stopPlayRemoteAudioStream(streamId)
            }
        })

        modules.add(ApiInfo("takeSnapshot") {
            selectStream(roomId, engine, childFragmentManager, "takeSnapshot") { stream ->
                val streamId = stream.streamId
                var logDir = if (requireActivity().obbDir != null) {
                    requireActivity().obbDir.absolutePath
                } else {
                    requireActivity().filesDir.absolutePath
                }
                val filePath = logDir + java.io.File.separator + "FcrCore" + java.io.File.separator + "snapshot.jpg"
                roomControl.getStreamControl()
                    ?.takeSnapshot(streamId, filePath, object : FcrCallback<FcrSnapshotInfo> {
                        override fun onSuccess(res: FcrSnapshotInfo?) {
                            Trace.Builder(api).page(PAGE).info("takeSnapshot success streams:${res}").commit()
                        }

                        override fun onFailure(error: FcrError) {
                            super.onFailure(error)
                            Trace.Builder(api).page(FcrTestUserFragment.PAGE).info("takeSnapshot error:${error}")
                                .commit()
                        }
                    })
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        roomControl.getStreamControl()?.removeObserver(observer)
    }
}