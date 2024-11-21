package io.agora.educore.test.setting

import android.os.Bundle
import android.view.View
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import io.agora.core.common.helper.SPreferenceManager
import io.agora.core.common.http.utils.FcrHttpEnv
import io.agora.core.common.http.utils.FcrHttpEnvUtils
import io.agora.core.common.utils.CommonConstants
import io.agora.core.common.utils.SkinUtils
import io.agora.educore.example.R
import io.agora.educore.example.databinding.FragmentSettingBinding

/**
 * 创建房间
 * https://yapi.sh2.agoralab.co/project/53/interface/api/1929
 *
 * 加入房间
 * https://yapi.sh2.agoralab.co/project/53/interface/api/1939
 *
 * 房间列表
 * https://yapi.sh2.agoralab.co/project/53/interface/api/1924
 *
 * 预检
 * https://yapi.sh2.agoralab.co/project/53/interface/api/1974
 */
class FcrSettingFragment : Fragment(R.layout.fragment_setting) {
    lateinit var binding: FragmentSettingBinding

    private val envMap: ArrayMap<FcrHttpEnv, Int> = ArrayMap<FcrHttpEnv, Int>().apply {
        put(FcrHttpEnv.TEST, R.id.agora_test_test)
        put(FcrHttpEnv.DEV, R.id.agora_test_env_dev)
        put(FcrHttpEnv.PRE, R.id.agora_test_env_pre)
        put(FcrHttpEnv.PROD, R.id.agora_test_env_prod)
        put(FcrHttpEnv.PRIVATE, R.id.agora_test_env_private)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = FragmentSettingBinding.bind(view)
        initView()
        testSetEnv()
        testNight()
    }

    private fun initView() {
        binding.rgEnv.check(
            envMap[SPreferenceManager.getObject<FcrHttpEnv>(
                CommonConstants.KEY_SP_ENV,
                FcrHttpEnv::class.java
            ) ?: FcrHttpEnv.TEST]!!
        )
    }


    private fun testNight() {
        val isCheck = SPreferenceManager.get(CommonConstants.KEY_SP_NIGHT, false)
        binding.cbNightMode.isChecked = isCheck
        binding.cbNightMode.setOnCheckedChangeListener { buttonView, isChecked ->
            SPreferenceManager.put(CommonConstants.KEY_SP_NIGHT, isChecked)
            SkinUtils.setNightMode(isChecked)
        }
    }

    private fun testSetEnv() {
        SPreferenceManager.put(CommonConstants.KEY_SP_USE_OPEN_TEST_MODE, true)
        val envInfo: FcrHttpEnv? = SPreferenceManager.getObject(CommonConstants.KEY_SP_ENV, FcrHttpEnv::class.java)
        if (envInfo == null) {
            SPreferenceManager.putObject(CommonConstants.KEY_SP_ENV, FcrHttpEnv.PROD)
        }
        binding.rgEnv.setOnCheckedChangeListener { group, checkedId ->
            envMap.forEach {
                if (checkedId == it.value) {
                    FcrHttpEnvUtils.setEnv(it.key)
                }
            }
        }
        setCheck()
    }

    private fun setCheck() {
        val envInfo = FcrHttpEnvUtils.getEnv(FcrHttpEnv.TEST)
        envMap.forEach {
            if (it.key == envInfo) {
                binding.rgEnv.check(it.value)
            }
        }
    }
}