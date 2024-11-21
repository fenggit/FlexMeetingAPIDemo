package io.agora.educore.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.shengwang.scene.FcrUISceneConfig
import io.agora.educore.test.tracer.ITracer
import io.agora.agoracore.core2.FcrCoreEngine
import io.agora.agoracore.core2.control.api.FcrBaseRoomControl
import io.agora.educore.test.tracer.Trace
import io.agora.educore.test.tracer.Tracer

/**
 * author : qinwei@agora.io
 * date : 2024/10/9 18:34
 * description :
 */
class FcrTestUISceneViewModel : ViewModel() {
    var config: FcrUISceneConfig? = null
    var engine: FcrCoreEngine? = null
    val message = MutableLiveData<String>()

    init {
        Tracer.inject(object : ITracer {
            override fun commit(trace: Trace) {
                message.value = trace.getPage() + "[" + trace.getCode() + "]" + trace.getInfo()
            }
        })
    }

    fun setCoreEngine(engine: FcrCoreEngine) {
        this.engine = engine
    }

    fun setFcrUISceneConfig(config: FcrUISceneConfig) {
        this.config = config
    }

    override fun onCleared() {
        super.onCleared()
        engine?.release()
    }
}