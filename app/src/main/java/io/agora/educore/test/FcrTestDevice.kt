package io.agora.educore.test

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import io.agora.agoracore.rte2.internal.bean.AgoraRtcDeviceInfo
import io.agora.core.common.log.LogX

/**
 * author : felix
 * date : 2024/10/30
 * description :
 */
object FcrTestDevice {
    fun getMicList(context: Context): List<AgoraRtcDeviceInfo> {
        val list = mutableListOf<AgoraRtcDeviceInfo>()
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)
            for (device in devices) {
                if (device.type == AudioDeviceInfo.TYPE_BUILTIN_MIC) {
                    list.add(AgoraRtcDeviceInfo("" + device.id, "" + device.productName))
                }
            }
        }
        if (list.isEmpty()) {
            list.add(AgoraRtcDeviceInfo("0", "mic"))
        }
        return list
    }

    fun getCameraList(context: Context): List<AgoraRtcDeviceInfo> {
        val list = mutableListOf<AgoraRtcDeviceInfo>()
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraIds = cameraManager.cameraIdList
            for ((index, cameraId) in cameraIds.withIndex()) {
                list.add(AgoraRtcDeviceInfo(cameraId, cameraId))
                LogX.d("CameraInfo", "Camera ID: $cameraId")
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        if (list.isEmpty()) {
            list.add(AgoraRtcDeviceInfo("1", "camera"))
        }
        return list
    }

    fun getSpeakerList(context: Context): List<AgoraRtcDeviceInfo> {
        val list = mutableListOf<AgoraRtcDeviceInfo>()
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (device in devices) {
                if (device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                    list.add(AgoraRtcDeviceInfo("" + device.id, "" + device.productName))
                }
            }
        }
        if (list.isEmpty()) {
            list.add(AgoraRtcDeviceInfo("0", "speaker"))
        }
        return list
    }


}