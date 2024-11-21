package io.agora.educore.test

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.ViewModelProvider
import cn.shengwang.http.FcrSceneRoomService
import cn.shengwang.http.bean.FcrSceneCreateRoomReq
import cn.shengwang.http.bean.FcrSceneCreateRoomRes
import cn.shengwang.http.bean.FcrSceneJoinRoomReq
import cn.shengwang.http.bean.FcrSceneJoinRoomRes
import cn.shengwang.scene.FcrUIScene
import cn.shengwang.scene.FcrUISceneConfig
import cn.shengwang.scene.helper.FcrHttpHelper
import cn.shengwang.scene.helper.FcrLunchConfigParameters
import io.agora.agoracore.core2.FcrCoreEngineObserver
import io.agora.agoracore.core2.bean.FcrConnectionState
import io.agora.agoracore.core2.bean.FcrDeviceType
import io.agora.agoracore.core2.bean.FcrMessage
import io.agora.agoracore.core2.bean.FcrUserRole
import io.agora.agoracore.core2.control.api.FcrBaseRoomControl
import io.agora.agoracore.core2.utils.FcrHashUtil
import io.agora.core.common.helper.SPreferenceManager
import io.agora.core.common.http.app.AppRetrofitManager
import io.agora.core.common.http.app.HttpCallback
import io.agora.core.common.http.bean.HttpBaseRes
import io.agora.core.common.http.utils.FcrHttpEnvUtils
import io.agora.core.common.log.LogX
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.example.R
import io.agora.educore.example.base.BaseActivity
import io.agora.educore.example.databinding.ActivitySceneTestBinding
import io.agora.educore.test.setting.FcrSettingFragment


/**
 * author : qinwei@agora.io
 * date : 2024/10/9 15:45
 * description : FcrCore API Test
 */
class FcrTestUISceneActivity : BaseActivity(), FcrRoomJoinFragment.OnRoomJoinListener {
    private lateinit var mFcrTestUISceneViewModel: FcrTestUISceneViewModel
    private lateinit var binding: ActivitySceneTestBinding

    companion object {
        const val TAG = "FcrTestUISceneActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySceneTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFcrTestUISceneViewModel = ViewModelProvider(this)[FcrTestUISceneViewModel::class.java]
        mFcrTestUISceneViewModel.message.observe(this) {
            if (binding.tvMessage.text.isEmpty()) {
                binding.tvMessage.append(it)
            } else {
                binding.tvMessage.append("\n" + it)
            }
            binding.root.post {
                binding.svMessage.fullScroll(View.FOCUS_DOWN)
            }
        }
        binding.btnInit.setOnClickListener {
            loadAppInfo()
        }
        binding.btnInit.performClick()
        supportFragmentManager.addOnBackStackChangedListener {
            supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 1)
        }
    }

    private fun loading() {
        binding.btnInit.isVisible = false
        binding.gLoading.isVisible = true
    }

    private fun fail() {
        binding.btnInit.isVisible = true
        binding.gLoading.isVisible = false
    }

    private fun notifyInitSuccess() {
        binding.btnInit.isVisible = false
        binding.gLoading.isVisible = false
    }

    private fun loadAppInfo() {
        loading()
        LogX.d(TAG, "createRoom")
        createRoom {
            if (it.isEmpty()) {
                fail()
            } else {
                LogX.d(TAG, "createRoom success $it")
                loadAppIdInfo(it)
            }
        }
    }

    private fun createRoom(callback: (String) -> Unit) {
        val roomName = "MyRoom" + System.currentTimeMillis()
        val startTime = System.currentTimeMillis() + 60 * 1000 * 60  // 1 hour later
        val endTime = startTime + 60 * 1000 * 45                     // 45 minutes
        val req = FcrSceneCreateRoomReq(roomName, startTime, endTime)
        val call = AppRetrofitManager.getService(FcrSceneRoomService::class.java).createRoom(req)
        AppRetrofitManager.Companion.exc(call, object : HttpCallback<HttpBaseRes<FcrSceneCreateRoomRes>>() {
            override fun onSuccess(result: HttpBaseRes<FcrSceneCreateRoomRes>?) {
                val roomId = result?.data?.roomId ?: return
                callback.invoke(roomId)
            }

            override fun onError(httpCode: Int, code: Int, message: String?) {
                super.onError(httpCode, code, message)
                // code == 1101021 房间号不存在
                Toast.makeText(this@FcrTestUISceneActivity, "error:$message", Toast.LENGTH_SHORT).show()
                callback.invoke("")
            }
        })
    }

    private fun loadAppIdInfo(roomId: String) {
        val userName = roomId + "_username"
        val userRole = FcrUserRole.HOST
        val userId = FcrHashUtil.md5(userName + userRole).lowercase()
        val req = FcrSceneJoinRoomReq(roomId, userRole.value, userId, userName)
        val call = AppRetrofitManager.getService(FcrSceneRoomService::class.java).joinRoom(req)
        AppRetrofitManager.Companion.exc(call, object : HttpCallback<HttpBaseRes<FcrSceneJoinRoomRes>>() {
            override fun onSuccess(result: HttpBaseRes<FcrSceneJoinRoomRes>?) {
                result?.data?.let {
                    val config = FcrUISceneConfig(it.appId, it.token, roomId, userId, userName, userRole)
                    config.roomName = it.roomDetail.roomName
                    config.startTime = it.roomDetail.startTime
                    config.endTime = it.roomDetail.endTime
                    SPreferenceManager.put("a.b.roomId", roomId)
                    if (FcrHttpEnvUtils.isPrivate()) {
                        config.parameters = FcrLunchConfigParameters.getLunchConfigParameters()
                    }
                    LogX.d(TAG, "initEngine")
                    initEngine(config)
                }
            }

            override fun onError(httpCode: Int, code: Int, message: String?) {
                super.onError(httpCode, code, message)
                // code == 1101021 房间号不存在
                Toast.makeText(this@FcrTestUISceneActivity, "error:$message", Toast.LENGTH_SHORT).show()
                fail()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.room_setting -> {
                //navigation(FcrSettingFragment::class.java)
            }

            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initEngine(config: FcrUISceneConfig) {
        FcrHttpHelper.initHttp(config)
        val engine = FcrUIScene.createEngine(this, config)
        mFcrTestUISceneViewModel.setFcrUISceneConfig(config)
        mFcrTestUISceneViewModel.setCoreEngine(engine)
        engine.addObserver(object : FcrCoreEngineObserver {
            override fun onConnectionStateUpdated(state: FcrConnectionState) {
                if (state == FcrConnectionState.DISCONNECTED) {
                    Toast.makeText(this@FcrTestUISceneActivity, "rtm aborted , leave room", Toast.LENGTH_LONG).show()
                }
            }

            override fun onPeerMessageReceived(message: FcrMessage) {}
        })
        LogX.d(TAG, "login")
        engine.login(object : FcrCallback<Any> {
            override fun onSuccess(res: Any?) {
                Log.e(TAG, "login rtm success")
                engine.getMobileMediaControl().openDevice(FcrDeviceType.CAMERA)
                engine.getMobileMediaControl().openDevice(FcrDeviceType.MICROPHONE)
                runOnUiThread {
                    notifyInitSuccess()
                    navigation(FcrRoomJoinFragment::class.java)
                }
            }

            override fun onFailure(error: FcrError) {
                super.onFailure(error)
                LogX.d(TAG, "onFailure $error")
                runOnUiThread {
                    Toast.makeText(this@FcrTestUISceneActivity, "error:${error.message}", Toast.LENGTH_SHORT).show()
                    fail()
                }
            }
        })
    }

    fun navigation(clazz: Class<out Fragment>, bundle: Bundle? = null) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_content, clazz.newInstance().apply {
                if (bundle != null) this.arguments = bundle
            }).addToBackStack(clazz.simpleName).commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            mFcrTestUISceneViewModel.engine?.release()
            finish()
        }
    }

    override fun onRoomJoinSuccess(roomControl: FcrBaseRoomControl<*>) {
        //构建房间信息
        navigation(FcrRoomListFragment::class.java, Bundle().apply {
            this.putString("roomId", roomControl.getRoomInfo().roomId)
        })
    }
}