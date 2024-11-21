package io.agora.educore.test

import android.util.DisplayMetrics
import android.util.Size
import io.agora.agoracore.core2.bean.FcrCapability
import io.agora.agoracore.core2.bean.FcrDeviceType
import io.agora.agoracore.core2.bean.FcrMediaSourceState
import io.agora.agoracore.core2.bean.FcrScreenCaptureParams
import io.agora.agoracore.core2.bean.FcrVideoRenderConfig
import io.agora.agoracore.core2.bean.FrcAudioOutputRouting
import io.agora.agoracore.core2.control.observer.FcrMobileMediaObserver
import io.agora.core.common.log.LogX
import io.agora.educore.test.tracer.Trace

/**
 * author : chenbinhang@agora.io
 * date : 2024/10/14
 * description :
 */
class FcrTestMediaFragment : FcrTestBaseListFragment() {
    companion object {
        const val PAGE = "Media"
    }

    private val observer = object : FcrMobileMediaObserver() {
        /**
         * 摄像头设备状态变更
         */
        override fun onCameraStateUpdated(state: FcrMediaSourceState) {
            Trace.Builder("onCameraStateUpdated").page(PAGE).info("callback:  state:${state}").commit()
        }

        /**
         * 麦克风设备状态变更
         */
        override fun onMicrophoneStateUpdated(state: FcrMediaSourceState) {
            Trace.Builder("onMicrophoneStateUpdated").page(PAGE).info("callback:  state:${state}").commit()
        }

        /**
         * 扬声器设备状态变更
         */
        override fun onSpeakerStateUpdated(state: FcrMediaSourceState) {
            Trace.Builder("onSpeakerStateUpdated").page(PAGE).info("callback:  state:${state}").commit()
        }

        /**
         * 本地输入音量变更 [0,100]
         */
        override fun onMicrophoneVolumeIndicationUpdated(volume: Int) {
            MessageProcessor.getInstance().putAction {
                LogX.i("onMicrophoneVolumeIndicationUpdated volume:$volume")
            }
        }

        /**
         * 扬声器、本机麦克风、耳机设备路由回调
         */
        override fun onAudioRouteUpdated(state: FrcAudioOutputRouting) {
            Trace.Builder("onAudioRouteUpdated").page(PAGE).info("callback:  state:${state}").commit()
        }

        /**
         * 屏幕录制状态变更
         * @param state 屏幕共享状态
         */
        override fun onScreenCaptureStateUpdated(state: FcrMediaSourceState) {
            Trace.Builder("onScreenCaptureStateUpdated").page(PAGE).info("callback:  state:${state}").commit()
        }

        /**
         * 本地输出音量变更 [0,100]
         */
        override fun onOutputVolumeIndicationUpdated(volume: Int) {
            Trace.Builder("onOutputVolumeIndicationUpdated").page(PAGE).info("callback:  state:${volume}").commit()
        }
    }
//    private val previewFragment = FcrPreviewFragment()

    override fun initData() {
        super.initData()
//        val previewTag = "FcrTestMediaFragment"
//        childFragmentManager.findFragmentByTag(previewTag)?.apply {
//            childFragmentManager.beginTransaction().remove(this).commit()
//        }
//        childFragmentManager.beginTransaction().add(previewFragment, previewTag).commit()
        engine.getMobileMediaControl().addObserver(observer)
        modules.add(ApiInfo("openDevice  camera") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val fcrError = engine.getMobileMediaControl().openDevice(FcrDeviceType.CAMERA)
            Trace.Builder(api).page(PAGE).info("openDevice  camera fcrError:$fcrError").commit()
        })
        modules.add(ApiInfo("openDevice  microphone") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val fcrError = engine.getMobileMediaControl().openDevice(FcrDeviceType.MICROPHONE)
            Trace.Builder(api).page(PAGE).info("openDevice  camera fcrError:$fcrError").commit()
        })
        modules.add(ApiInfo("openDevice  speaker") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val fcrError = engine.getMobileMediaControl().openDevice(FcrDeviceType.SPEAKER)
            Trace.Builder(api).page(PAGE).info("openDevice  camera fcrError:$fcrError").commit()
        })

        modules.add(ApiInfo("closeDevice  camera") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val fcrError = engine.getMobileMediaControl().closeDevice(FcrDeviceType.CAMERA)
            Trace.Builder(api).page(PAGE).info("closeDevice  camera fcrError:$fcrError").commit()
        })
        modules.add(ApiInfo("closeDevice  microphone") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val fcrError = engine.getMobileMediaControl().closeDevice(FcrDeviceType.MICROPHONE)
            Trace.Builder(api).page(PAGE).info("closeDevice  microphone fcrError:$fcrError").commit()
        })
        modules.add(ApiInfo("closeDevice  speaker") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val fcrError = engine.getMobileMediaControl().closeDevice(FcrDeviceType.SPEAKER)
            Trace.Builder(api).page(PAGE).info("closeDevice  speaker fcrError:$fcrError").commit()
        })



        modules.add(ApiInfo("getDeviceId  camera") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val deviceId = engine.getMobileMediaControl().getDeviceId(FcrDeviceType.CAMERA)
            Trace.Builder(api).page(PAGE).info("getDeviceId  camera deviceId:$deviceId").commit()
        })
        modules.add(ApiInfo("getDeviceId  microphone") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val deviceId = engine.getMobileMediaControl().getDeviceId(FcrDeviceType.MICROPHONE)
            Trace.Builder(api).page(PAGE).info("getDeviceId  camera microphone:$deviceId").commit()
        })
        modules.add(ApiInfo("getDeviceId  speaker") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val deviceId = engine.getMobileMediaControl().getDeviceId(FcrDeviceType.SPEAKER)
            Trace.Builder(api).page(PAGE).info("getDeviceId  speaker deviceId:$deviceId").commit()
        })



        modules.add(ApiInfo("getDeviceState  camera") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val deviceState = engine.getMobileMediaControl().getDeviceState(FcrDeviceType.CAMERA)
            Trace.Builder(api).page(PAGE).info("getDeviceId  camera deviceState:$deviceState").commit()
        })
        modules.add(ApiInfo("getDeviceId  microphone") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val deviceState = engine.getMobileMediaControl().getDeviceState(FcrDeviceType.MICROPHONE)
            Trace.Builder(api).page(PAGE).info("getDeviceId  microphone deviceState:$deviceState").commit()
        })
        modules.add(ApiInfo("getDeviceId  speaker") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val deviceState = engine.getMobileMediaControl().getDeviceState(FcrDeviceType.SPEAKER)
            Trace.Builder(api).page(PAGE).info("getDeviceId  speaker deviceState:$deviceState").commit()
        })



        modules.add(ApiInfo("adjustOutputVolume :70") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val err = engine.getMobileMediaControl().adjustOutputVolume(70)
            Trace.Builder(api).page(PAGE).info("adjustOutputVolume:$err").commit()
        })

        modules.add(ApiInfo("switchCamera") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val err = engine.getMobileMediaControl().switchCamera()
            Trace.Builder(api).page(PAGE).info("switchCamera:$err").commit()
        })

        //getCameraPosition
        modules.add(ApiInfo("getCameraPosition") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val cameraPosition = engine.getMobileMediaControl().getCameraPosition()
            Trace.Builder(api).page(PAGE).info("getCameraPosition:$cameraPosition").commit()
        })

        //startCameraTest
        //stopCameraTest  这两个函数需要成对使用，单测会有问题，不放在这里调用

        val config = FcrVideoRenderConfig()
//        modules.add(ApiInfo("startCameraPreview") {
//            Trace.Builder(api).page(PAGE).info("call").commit()
//            val err = engine.getMobileMediaControl().startCameraPreview(
//                videoView = previewFragment.preview,
//                config = config,
//                listener = null
//            )
//            Trace.Builder(api).page(PAGE).info("startCameraPreview:$err").commit()
//        })


        modules.add(ApiInfo("stopCameraPreview") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val err = engine.getMobileMediaControl().stopCameraPreview()
            Trace.Builder(api).page(PAGE).info("stopCameraPreview err:$err").commit()
        })

        modules.add(ApiInfo("isCapabilitySupported ") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val boolean = engine.getMobileMediaControl().isCapabilitySupported(requireContext(), FcrCapability.SCREEN_CAPTURE)
            Trace.Builder(api).page(PAGE).info("isCapabilitySupported SCREEN_CAPTURE:$boolean").commit()
        })

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val size = Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        val params = FcrScreenCaptureParams(size, frameRate = 30, bitrate = 600, hasAudio = false)
        modules.add(ApiInfo("startScreenCapture") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val err = engine.getMobileMediaControl().startScreenCapture(params)
            Trace.Builder(api).page(PAGE).info("startScreenCapture err:$err").commit()
        })
        //
        modules.add(ApiInfo("stopScreenCapture") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val err = engine.getMobileMediaControl().stopScreenCapture()
            Trace.Builder(api).page(PAGE).info("stopScreenCapture err:$err").commit()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        engine.getMobileMediaControl().removeObserver(observer)
    }
}