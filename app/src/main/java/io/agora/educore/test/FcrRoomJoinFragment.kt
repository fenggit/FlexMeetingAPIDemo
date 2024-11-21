package io.agora.educore.test

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.shengwang.http.FcrSceneRoomService
import cn.shengwang.http.bean.FcrSceneCreateRoomReq
import cn.shengwang.http.bean.FcrSceneCreateRoomRes
import cn.shengwang.http.bean.FcrSceneJoinRoomReq
import cn.shengwang.http.bean.FcrSceneJoinRoomRes
import cn.shengwang.scene.FcrUISceneConfig
import cn.shengwang.scene.FcrUISceneCreator
import cn.shengwang.scene.helper.FcrLunchConfigParameters
import com.permissionx.guolindev.PermissionX
import io.agora.agoracore.core2.bean.FcrRoomControlCreateConfig
import io.agora.agoracore.core2.bean.FcrRoomJoinOptions
import io.agora.agoracore.core2.bean.FcrRoomType
import io.agora.agoracore.core2.bean.FcrStreamJoinConfig
import io.agora.agoracore.core2.bean.FcrStreamType
import io.agora.agoracore.core2.bean.FcrUserRole
import io.agora.agoracore.core2.control.api.FcrBaseRoomControl
import io.agora.agoracore.core2.utils.FcrHashUtil
import io.agora.core.common.helper.SPreferenceManager
import io.agora.core.common.http.app.AppRetrofitManager
import io.agora.core.common.http.app.HttpCallback
import io.agora.core.common.http.bean.HttpBaseRes
import io.agora.core.common.http.utils.FcrHttpEnvUtils
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.example.R
import io.agora.educore.example.databinding.FragmentRoomJoinBinding

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
class FcrRoomJoinFragment : Fragment(R.layout.fragment_room_join) {
    private lateinit var mFcrTestUISceneViewModel: FcrTestUISceneViewModel
    private var listener: OnRoomJoinListener? = null
    lateinit var binding: FragmentRoomJoinBinding
    private var progressDialog: ProgressDialog? = null
    private val tag = "FcrRoomJoinFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRoomJoinBinding.bind(view)
        initView()
        initData()
    }

    private fun initData() {
        mFcrTestUISceneViewModel = ViewModelProvider(requireActivity())[FcrTestUISceneViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 初始化加载框
        progressDialog = ProgressDialog(requireContext()).apply {
            setTitle("加载中")
            setMessage("请稍候...")
            setCancelable(true)
        }
        parentFragment?.let {
            if (it is OnRoomJoinListener) {
                this.listener = it
                return
            }
        }
        if (context is OnRoomJoinListener) {
            this.listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun showLoading() {
        progressDialog?.show()
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
    }

    private fun initView() {
        val roomId: String = SPreferenceManager.get("a.b.roomId", null) ?: ""
        binding.etRoomId.setText(roomId)
        binding.btnCreateScene.setOnClickListener {
            createRoom()
        }
        binding.btnScene.setOnClickListener {
            PermissionX.init(this).permissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        loadRoomInfo {
                            joinRoom(it)
                        }
                    } else {
                        Toast.makeText(requireContext(), "no permissions", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.btnGetDeviceList.setOnClickListener {
            FcrTestDevice.getMicList(requireContext()).forEach {
                Log.e(tag, "mic: ${it.deviceId} ${it.deviceName}")
            }
            FcrTestDevice.getCameraList(requireContext()).forEach {
                Log.e(tag, "camera: ${it.deviceId} ${it.deviceName}")
            }
            FcrTestDevice.getSpeakerList(requireContext()).forEach {
                Log.e(tag, "speaker: ${it.deviceId} ${it.deviceName}")
            }
        }
    }

    private fun joinRoom(sceneConfig: FcrUISceneConfig) {
        val engine = mFcrTestUISceneViewModel.engine ?: return
        val roomConfig = FcrRoomControlCreateConfig(sceneConfig.roomId, FcrRoomType.MAIN_ROOM)
        val roomControl = engine.createRoomControl(roomConfig)!!
        val videoSourceId = engine.getMobileMediaControl().getCameraInfo().deviceId
        val audioSourceId = engine.getMobileMediaControl().getMicInfo().deviceId
        val createStreamConfigs = ArrayList<FcrStreamJoinConfig>()
        val streamInfo = FcrStreamJoinConfig(null, videoSourceId, audioSourceId, "Tom Stream", FcrStreamType.BOTH)
        createStreamConfigs.add(streamInfo)
        val option = FcrRoomJoinOptions(
            sceneConfig.userName,
            sceneConfig.userRole,
            sceneConfig.userProperties,
            sceneConfig.token,
            sceneConfig.streamLatency,
        )
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("加入房间")
        progressDialog.show()
        roomControl.join(option, object : FcrCallback<Unit> {
            override fun onSuccess(res: Unit?) {
                binding.root.post {
                    progressDialog.dismiss()
                    Log.e(FcrTestUISceneActivity.TAG, "join room success")
                    listener?.onRoomJoinSuccess(roomControl)
                    parentFragmentManager.beginTransaction().remove(this@FcrRoomJoinFragment).commit()
                }
            }

            override fun onFailure(error: FcrError) {
                val message = "preCheck:" + when (error.code) {
                    FcrUISceneCreator.SCENE_STATE_KICK_OUT -> getString(io.agora.agoracore.R.string.room_end)
                    FcrUISceneCreator.SCENE_STATE_END -> getString(io.agora.agoracore.R.string.room_end)
                    FcrUISceneCreator.SCENE_STATE_FULL -> error.message + "(${error.code})"
                    else -> {
                        error.message + "(${error.code})"
                    }
                }
                progressDialog.dismiss()
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                Log.e(FcrTestUISceneActivity.TAG, "join room error")
            }
        })
    }

    /**
     * 创建房间和加入房间的 userId 要一样
     */
    private fun createRoom() {
        showLoading()
        val roomName = "MyRoom" + System.currentTimeMillis()
        val startTime = System.currentTimeMillis() + 60 * 1000 * 60  // 1 hour later
        val endTime = startTime + 60 * 1000 * 45                     // 45 minutes
        val req = FcrSceneCreateRoomReq(roomName, startTime, endTime)
        val call = AppRetrofitManager.getService(FcrSceneRoomService::class.java).createRoom(req)
        AppRetrofitManager.Companion.exc(call, object : HttpCallback<HttpBaseRes<FcrSceneCreateRoomRes>>() {
            override fun onSuccess(result: HttpBaseRes<FcrSceneCreateRoomRes>?) {
                result?.data?.let {
                    hideLoading()
                    Toast.makeText(requireActivity(), "create room success", Toast.LENGTH_SHORT).show()
                    binding.tvRoomInfo.text = "create room success roomId: ${it.roomId}"
                    binding.etRoomId.setText(it.roomId)
                }
            }

            override fun onError(httpCode: Int, code: Int, message: String?) {
                super.onError(httpCode, code, message)
                // code == 1101021 房间号不存在
                Toast.makeText(requireContext(), "error:$message", Toast.LENGTH_SHORT).show()
                hideLoading()
            }
        })
    }

    private fun loadRoomInfo(callback: (FcrUISceneConfig) -> Unit) {
        showLoading()
        val roomId = binding.etRoomId.text.toString()
        val userName = binding.etUsername.text.toString().trim()
        val userRole = when (binding.rgRole.checkedRadioButtonId) {
            R.id.rb_host -> FcrUserRole.HOST
            else -> FcrUserRole.PARTICIPANT
        }
        val userId = FcrHashUtil.md5(userName + userRole).lowercase()
        // 1、get token & appid
        val req = FcrSceneJoinRoomReq(roomId, userRole.value, userId, userName)
        val call = AppRetrofitManager.getService(FcrSceneRoomService::class.java).joinRoom(req)
        AppRetrofitManager.Companion.exc(call, object : HttpCallback<HttpBaseRes<FcrSceneJoinRoomRes>>() {
            override fun onSuccess(result: HttpBaseRes<FcrSceneJoinRoomRes>?) {
                result?.data?.let {
                    hideLoading()
                    val config = FcrUISceneConfig(it.appId, it.token, roomId, userId, userName, userRole)
                    config.roomName = it.roomDetail.roomName
                    config.startTime = it.roomDetail.startTime
                    config.endTime = it.roomDetail.endTime
                    SPreferenceManager.put("a.b.roomId", roomId)
                    if (FcrHttpEnvUtils.isPrivate()) {
                        config.parameters = FcrLunchConfigParameters.getLunchConfigParameters()
                    }
                    binding.root.post {
                        callback.invoke(config)
                    }
                }
            }

            override fun onError(httpCode: Int, code: Int, message: String?) {
                super.onError(httpCode, code, message)
                // code == 1101021 房间号不存在
                Toast.makeText(requireContext(), "error:$message", Toast.LENGTH_SHORT).show()
                hideLoading()
            }
        })
    }

    interface OnRoomJoinListener {
        fun onRoomJoinSuccess(roomControl: FcrBaseRoomControl<*>)
    }
}