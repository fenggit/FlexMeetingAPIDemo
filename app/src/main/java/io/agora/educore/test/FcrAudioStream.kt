package io.agora.educore.test

import io.agora.agoracore.core2.FcrCoreEngine
import io.agora.agoracore.core2.bean.FcrStreamEvent
import io.agora.agoracore.core2.control.observer.FcrStreamObserver

/**
 * author : felix
 * date : 2024/4/23
 * description : 音频流管理
 */
class FcrAudioStream(var coreEngine: FcrCoreEngine) {

    val streamObj = object : FcrStreamObserver {
        override fun onStreamsAdded(roomId: String, event: List<FcrStreamEvent>) {
            updatePlayStream(event)
        }

        override fun onStreamsRemoved(roomId: String, event: List<FcrStreamEvent>) {
            event.forEach {
                coreEngine.getRoomControl()?.getStreamControl()?.stopPlayRemoteAudioStream(it.modifiedStream.streamId)
            }
        }

        override fun onStreamsUpdated(roomId: String, event: List<FcrStreamEvent>) {
            updatePlayStream(event)
        }

        fun updatePlayStream(event: List<FcrStreamEvent>?) {
            event?.forEach {
                val streamInfo = it.modifiedStream
                streamInfo.streamId.let { streamId ->
                    if (streamInfo.isMicAudioEnable() || streamInfo.isScreenAudioEnable()) {
                        coreEngine.getRoomControl()?.getStreamControl()?.startPlayRemoteAudioStream(streamId)
                    } else {
                        coreEngine.getRoomControl()?.getStreamControl()?.stopPlayRemoteAudioStream(streamId)
                    }
                }
            }
        }
    }

    fun setAudioStreamListener() {
        coreEngine.getRoomControl()?.getStreamControl()?.getStreamList()?.forEach{streamInfo->
            if (streamInfo.isMicAudioEnable() || streamInfo.isScreenAudioEnable()) {
                coreEngine.getRoomControl()?.getStreamControl()?.startPlayRemoteAudioStream(streamInfo.streamId)
            } else {
                coreEngine.getRoomControl()?.getStreamControl()?.stopPlayRemoteAudioStream(streamInfo.streamId)
            }
        }
        coreEngine.getRoomControl()?.getStreamControl()?.addObserver(streamObj)
    }

    fun removeAudioStreamListener() {
        coreEngine.getRoomControl()?.getStreamControl()?.removeObserver(streamObj)
    }
}