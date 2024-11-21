package io.agora.educore.test

import io.agora.agoracore.core2.control.observer.FcrMonitorObserver
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.test.tracer.Trace

/**
 * author : chenbinhang@agora.io
 * date : 2024/10/14
 * description :
 */
class FcrTestMonitorFragment : FcrTestBaseListFragment() {
    companion object {
        const val PAGE = "Monitor"
    }

    private val observer = FcrMonitorObserver { data -> Trace.Builder("onMonitorDataUpdated").page(PAGE).info(data.toString()).commit() }

    override fun initData() {
        super.initData()
        engine.getMonitorControl().addObserver(observer)
        modules.add(ApiInfo("uploadLog") {
            Trace.Builder(api).page(PAGE).info("call").commit()
//            engine.getMonitorControl().uploadLog(object : FcrCallback<String> {
//                override fun onSuccess(res: String?) {
//                    Trace.Builder(api).page(PAGE).info("success: $res").commit()
//                }
//
//                override fun onFailure(error: FcrError) {
//                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
//                }
//            })
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        engine.getMonitorControl().removeObserver(observer)
    }
}