package cn.shengwang.scene.helper

import io.agora.core.common.http.core.FcrDomainManager
import io.agora.core.common.http.interceptor.FcrHttpHeaderManager
import cn.shengwang.scene.FcrUISceneConfig

/**
 * author : felix
 * date : 2024/3/5
 * description :
 */
object FcrHttpHelper {

    fun initHttp(config: FcrUISceneConfig) {
        FcrDomainManager.initDomain(config.region)
        setHttpHeader(config)
    }

    private fun setHttpHeader(config: FcrUISceneConfig) {
        val map = mutableMapOf<String, String>()
        map[FcrHttpHeaderManager.HEADER_X_AGORA_TOKEN] = config.token
        map[FcrHttpHeaderManager.HEADER_X_AGORA_UID] = config.userId
        map[FcrHttpHeaderManager.HEADER_TOKEN007] = FcrHttpHeaderManager.getToken(config.token)
        FcrHttpHeaderManager.setAllHeader(map)

//        CoreRetrofitManager.instance().setAllHeader(map)
//        AppRetrofitManager.instance().setAllHeader(map)
//        LogReportRetrofitManager.instance().setAllHeader(map)
    }
}