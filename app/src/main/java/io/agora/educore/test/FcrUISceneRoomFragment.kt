package io.agora.educore.test

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import io.agora.agoracore.core2.FcrCoreEngine
import io.agora.agoracore.core2.bean.FcrDeviceType
import io.agora.agoracore.core2.bean.FcrLocalRenderListener
import io.agora.agoracore.core2.bean.FcrStreamEvent
import io.agora.agoracore.core2.bean.FcrStreamInfo
import io.agora.agoracore.core2.bean.FcrVideoRenderConfig
import io.agora.agoracore.core2.bean.FcrVideoStreamType
import io.agora.agoracore.core2.control.observer.FcrStreamObserver
import io.agora.agoracore.rte2.pub.bean.AgoraVideoRenderMode
import io.agora.core.common.log.LogX
import io.agora.educore.example.R
import io.agora.educore.example.databinding.FragmentUiSceneRoomBinding
import io.agora.educore.example.databinding.LayoutWindowItemBinding

/**
 * author : qinwei@agora.io
 * date : 2024/10/14 19:59
 * description :
 */
class FcrUISceneRoomFragment : Fragment(R.layout.fragment_ui_scene_room) {
    private lateinit var roomId: String
    private lateinit var engine: FcrCoreEngine
    private lateinit var mFcrTestUISceneViewModel: FcrTestUISceneViewModel
    private lateinit var binding: FragmentUiSceneRoomBinding

    companion object {
        const val TAG = "FcrUISceneRoomFragment"
    }

    data class Clazz(val name: String, val fragment: Class<out Fragment>)

    private val modules = ArrayList<Clazz>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUiSceneRoomBinding.bind(view)
        mFcrTestUISceneViewModel = ViewModelProvider(requireActivity())[FcrTestUISceneViewModel::class.java]
        engine = mFcrTestUISceneViewModel.engine ?: return
        roomId = arguments?.getString("roomId") ?: return
        showMainRoom()
    }

    private fun notifyStreamsChanged() {
        windows.clear()
        windows.addAll(engine.getRoomControl(roomId)?.getStreamControl()
            ?.getStreamList()?.filter { it.owner.userId != engine.config.userId } ?: arrayListOf())
        adapter.notifyDataSetChanged()
    }

    private val windows = ArrayList<FcrStreamInfo>()
    private val adapter = object : RecyclerView.Adapter<WindowHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WindowHolder {
            return WindowHolder(LayoutWindowItemBinding.inflate(layoutInflater, parent, false))
        }

        override fun getItemCount(): Int {
            return windows.size
        }

        override fun onBindViewHolder(holder: WindowHolder, position: Int) {
            holder.onBind(position)
        }
    }

    inner class WindowHolder(private val itemBinding: LayoutWindowItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var item: FcrStreamInfo
        fun onBind(position: Int) {
            item = windows[position]
            itemBinding.tvUsername.text = item.owner.userName
            if (item.isCameraVideoEnable() || item.isScreenVideoEnable()) {
                val renderConfig = FcrVideoRenderConfig(AgoraVideoRenderMode.FIT)
                engine.getRoomControl(roomId)?.getStreamControl()!!.startRenderRemoteVideoStream(
                    item.streamId,
                    itemBinding.textureView,
                    renderConfig,
                    FcrVideoStreamType.LOW
                )
            } else {
                engine.getRoomControl(roomId)?.getStreamControl()!!.stopRenderRemoteVideoStream(item.streamId)
            }
        }
    }

    private fun showMainRoom() {
        //存下RoomControl
        engine.getMobileMediaControl().openDevice(FcrDeviceType.CAMERA)
        engine.getMobileMediaControl().openDevice(FcrDeviceType.MICROPHONE)
        engine.getMobileMediaControl().openDevice(FcrDeviceType.SPEAKER)
        FcrAudioStream(engine).setAudioStreamListener()

        engine.getMobileMediaControl()
            .startCameraPreview(binding.localVideoView, FcrVideoRenderConfig(), object : FcrLocalRenderListener {
                override fun onFirstLocalVideoFrameRendered(streamId: String, width: Int, height: Int) {
                    LogX.e("onFirstLocalVideoFrameRendered streamId=$streamId width=$width height=$height")
                }
            })

        modules.clear()
        modules.apply {
            add(Clazz("engine", FcrTestCoreEngineFragment::class.java))
            add(Clazz("mainRoom", FcrTestRoomControlFragment::class.java))
            add(Clazz("peerSession", FcrTestPeerSessionFragment::class.java))
            add(Clazz("roomSession", FcrTestRoomSessionFragment::class.java))
            add(Clazz("RoomConnector", FcrTestRoomConnectorControlFragment::class.java))
            add(Clazz("boardRoom", FcrTestBoardFragment::class.java))
            add(Clazz("monitor", FcrTestMonitorFragment::class.java))
            add(Clazz("media", FcrTestMediaFragment::class.java))
            add(Clazz("videoEffectEnhancer", FcrTestVideoEffectEnhancerFragment::class.java))
            add(Clazz("privilege", FcrTestPrivilegeFragment::class.java))
            add(Clazz("stream", FcrTestStreamFragment::class.java))
            add(Clazz("user", FcrTestUserFragment::class.java))
            add(Clazz("chat", FcrTestChatRoomFragment::class.java))
        }
        binding.viewpager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return modules.size
            }

            override fun createFragment(position: Int): Fragment {
                return modules[position].fragment.newInstance().apply {
                    arguments = Bundle().apply {
                        putString("roomId", roomId)
                    }
                }
            }
        }
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewpager2
        ) { tab, position -> tab.setText(modules[position].name) }.attach()
        fillWindows()
        //监听流加入
        mFcrTestUISceneViewModel.engine?.getRoomControl(roomId)?.getStreamControl()
            ?.addObserver(object : FcrStreamObserver {
                override fun onStreamsAdded(roomId: String, event: List<FcrStreamEvent>) {
                    super.onStreamsAdded(roomId, event)
                    LogX.d(TAG + this@FcrUISceneRoomFragment.roomId, "$roomId onStreamsAdded $event")
                    notifyStreamsChanged()
                }

                override fun onStreamsRemoved(roomId: String, event: List<FcrStreamEvent>) {
                    super.onStreamsRemoved(roomId, event)
                    LogX.d(
                        TAG + this@FcrUISceneRoomFragment.roomId,
                        "$roomId onStreamsRemoved $event"
                    )
                    notifyStreamsChanged()
                }

                override fun onStreamsUpdated(roomId: String, event: List<FcrStreamEvent>) {
                    super.onStreamsUpdated(roomId, event)
                    LogX.d(TAG + this@FcrUISceneRoomFragment.roomId, "$roomId onStreamsUpdated $event")
                    notifyStreamsChanged()
                }
            })
    }

    private fun fillWindows() {
        binding.rvWindow.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvWindow.adapter = adapter
        notifyStreamsChanged()
    }


    override fun onDestroy() {
        super.onDestroy()
        engine.getRoomControl(roomId)?.leave()
    }
}