package io.agora.educore.test

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.shengwang.scene.FcrUIExitReason
import cn.shengwang.scene.FcrUIScene
import cn.shengwang.scene.FcrUISceneConfig
import cn.shengwang.scene.FcrUISceneCreator
import cn.shengwang.scene.FcrUISceneObserver
import com.google.android.material.tabs.TabLayoutMediator
import io.agora.agoracore.core2.FcrCoreEngine
import io.agora.agoracore.core2.control.api.FcrBaseRoomControl
import io.agora.agoracore.core2.control.api.FcrMainRoomControl
import io.agora.agoracore.core2.control.observer.FcrMainRoomObserver
import io.agora.core.common.obs.FcrError
import io.agora.educore.example.R
import io.agora.educore.example.databinding.FragmentRoomListBinding

/**
 * author : qinwei@agora.io
 * date : 2024/10/23 16:54
 * description :
 */
class FcrRoomListFragment : Fragment(R.layout.fragment_room_list), FcrRoomJoinFragment.OnRoomJoinListener {
    private lateinit var binding: FragmentRoomListBinding
    private lateinit var engine: FcrCoreEngine
    private lateinit var sceneConfig: FcrUISceneConfig
    private lateinit var mFcrTestUISceneViewModel: FcrTestUISceneViewModel

    data class RoomClazz(val roomId: String, val clazz: Class<FcrUISceneRoomFragment>)

    private val modules = ArrayList<RoomClazz>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = FragmentRoomListBinding.bind(view)
        mFcrTestUISceneViewModel = ViewModelProvider(requireActivity())[FcrTestUISceneViewModel::class.java]
        engine = mFcrTestUISceneViewModel.engine ?: return
        sceneConfig = mFcrTestUISceneViewModel.config ?: return
        val roomId = arguments?.getString("roomId") ?: ""
        addRoomObserver(roomId)
        modules.add(RoomClazz(roomId, FcrUISceneRoomFragment::class.java))
        binding.viewpager2.isNestedScrollingEnabled = false
        binding.viewpager2.isUserInputEnabled = false
        binding.viewpager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return modules.size
            }

            override fun createFragment(position: Int): Fragment {
                val item = modules[position]
                val instance = item.clazz.newInstance()
                instance.arguments = Bundle().apply {
                    putString("roomId", modules[position].roomId)
                }
                return instance
            }
        }
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewpager2
        ) { tab, position -> tab.setText(modules[position].roomId) }.attach()
    }

    private fun addRoomObserver(roomId: String) {
        FcrUISceneCreator.addObserverOfUIScene(roomId, object : FcrUISceneObserver {
            override fun onExited(roomId: String, reason: FcrUIExitReason) {
                //离开房间
                modules.removeIf { it.roomId == roomId }
                binding.viewpager2.adapter?.notifyDataSetChanged()
                FcrUISceneCreator.removeObserverOfUIScene(roomId)
            }

            override fun onJoinSucceed(roomId: String) {

            }

            override fun onJoinFailed(roomId: String, error: FcrError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_room, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.room_add) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fl_content, FcrRoomJoinFragment())
                .commit()
        }
        return true
    }

    override fun onRoomJoinSuccess(roomControl: FcrBaseRoomControl<*>) {
        val roomId = roomControl.getRoomInfo().roomId ?: ""
        addRoomObserver(roomId)
        modules.add(RoomClazz(roomId, FcrUISceneRoomFragment::class.java))
        binding.viewpager2.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        FcrUISceneCreator.exit()
        engine.clearRoomControls()
    }
}