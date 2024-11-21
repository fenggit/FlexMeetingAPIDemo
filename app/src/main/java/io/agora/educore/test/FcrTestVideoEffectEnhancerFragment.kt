package io.agora.educore.test

import io.agora.agoracore.core2.bean.FcrBeautyOptions
import io.agora.agoracore.core2.bean.FcrVirtualBackgroundOptions
import io.agora.educore.example.utils.FileUtils
import io.agora.educore.test.tracer.Trace

/**
 * author : qinwei@agora.io
 * date : 2024/10/22 16:05
 * description :
 */
class FcrTestVideoEffectEnhancerFragment : FcrTestBaseListFragment() {
    companion object {
        const val PAGE = "VideoEffectEnhancer"
    }

    override fun initData() {
        super.initData()
        val videoEffect = engine.getMobileMediaControl().getVideoEffectEnhancer()
        modules.add(ApiInfo("setBeautyOptions") {
            val beautyOptions = FcrBeautyOptions()
            beautyOptions.rednessLevel = 0.5f
            beautyOptions.lighteningLevel = 0.8f
            beautyOptions.rednessLevel = 0.5f
            beautyOptions.sharpnessLevel = 0.5f
            Trace.Builder(api).page(PAGE).info(videoEffect.setBeautyOptions(beautyOptions).toString()).commit()
        })
        modules.add(ApiInfo("enableBeauty") {
            Trace.Builder(api).page(PAGE).info(videoEffect.enableBeauty().toString()).commit()
        })
        modules.add(ApiInfo("disableBeauty") {
            Trace.Builder(api).page(PAGE).info(videoEffect.disableBeauty().toString()).commit()
        })
        modules.add(ApiInfo("setVirtualBackgroundOptions") {
            val imagePath = requireContext().externalCacheDir?.path ?: ""
            val imageName = "test_img.png"
            FileUtils.copyFilesFromAssets(requireContext(), imageName, imagePath)
            val bgOption = FcrVirtualBackgroundOptions()
            bgOption.source = "$imagePath/$imageName"
            Trace.Builder(api).page(PAGE).info(videoEffect.setVirtualBackgroundOptions(bgOption).toString()).commit()
        })
        modules.add(ApiInfo("enableVirtualBackground") {
            Trace.Builder(api).page(PAGE).info(videoEffect.enableVirtualBackground().toString()).commit()
        })
        modules.add(ApiInfo("disableVirtualBackground") {
            Trace.Builder(api).page(PAGE).info(videoEffect.disableVirtualBackground().toString()).commit()
        })
        modules.add(ApiInfo("checkSatisfyVirtualBackgroundRequirements") {
            Trace.Builder(api).page(PAGE).info(videoEffect.checkSatisfyVirtualBackgroundRequirements().toString())
                .commit()
        })
        modules.add(ApiInfo("checkSatisfyBeautyRequirements") {
            Trace.Builder(api).page(PAGE).info(videoEffect.checkSatisfyBeautyRequirements().toString()).commit()
        })
    }
}