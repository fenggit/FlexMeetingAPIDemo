package cn.shengwang.scene.helper

import io.agora.core.common.http.bean.FcrRegion
import io.agora.core.common.utils.CommonConstants

object FcrLunchConfigParameters {
    private const val VID = "434183"

    fun getLunchConfigParameters(): HashMap<String, Any> {
        val map = hashMapOf<String, Any>()
        map[CommonConstants.KEY_CORE] = hashMapOf<String, Any>().apply {
            //如果设置environment,加入房间之前会切到指定环境
//            this[environment] = environment
            this["console"] = 0
            this["rtcRegion"] = FcrRegion.CN
            this["rtmRegion"] = FcrRegion.CN
            this["coreIpList"] = arrayListOf("https://api-solutions-private.agoralab.co")
            this["netlessBoardIpList"] = arrayListOf("")
            this["easemobChatIpList"] = arrayListOf("180.184.183.69:6717")
            this["easemobRestIpList"] = arrayListOf("https://zim-rtc.easemob.com:12000")
        }
        map["rte"] = hashMapOf<String, Any>().apply {
            this["rteIpList"] = arrayListOf("https://api-solutions-private.agoralab.co")
            this["rtcIpList"] = arrayListOf("60.191.137.131")
            this["rtcVerifyDomainName"] = "ap.130451.agora.local"
            this["rtmIpList"] = arrayListOf("private-rtc.ap.staging-1-aws.myagoralab.com")
        }
        return map
    }
}