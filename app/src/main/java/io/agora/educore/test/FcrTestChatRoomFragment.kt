package io.agora.educore.test

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import io.agora.agoracore.core2.bean.FcrUserInfo
import io.agora.agoracore.core2.control.chat.FcrChatRoomConnectionState
import io.agora.agoracore.core2.control.chat.FcrChatRoomControlObserver
import io.agora.agoracore.core2.control.chat.FcrChatRoomMessageType
import io.agora.agoracore.core2.control.chat.FcrChatRoomReceiveBaseMessage
import io.agora.agoracore.core2.control.chat.FcrChatRoomReceiveCustomMessage
import io.agora.agoracore.core2.control.chat.FcrChatRoomReceiveImageMessage
import io.agora.agoracore.core2.control.chat.FcrChatRoomReceiveTextMessage
import io.agora.agoracore.core2.control.chat.FcrChatRoomSendBaseMessage
import io.agora.agoracore.core2.control.chat.FcrChatRoomSendTextMessage
import io.agora.agoracore.core2.control.chat.FcrChatRoomUserInfo
import io.agora.agoracore.core2.control.chat.FcrValueCallback

import io.agora.educore.test.tracer.Trace
import io.agora.educore.test.view.InputDialogFragment
import io.agora.educore.test.view.ListFragmentDialog

/**
 * author : chenbinhang@agora.io
 * date : 2024/10/14
 * description :
 */
class FcrTestChatRoomFragment : FcrTestBaseListFragment() {
    companion object {
        private const val PAGE = "ChatRoom"
    }

    private val observer = object : FcrChatRoomControlObserver {
        override fun onConnectionStateChanged(roomId: String, state: FcrChatRoomConnectionState) {
            Trace.Builder("onConnectionStateChanged").page(FcrTestRoomControlFragment.PAGE).info("roomId:$roomId state:$state").commit()
        }

        override fun onTextMessageReceived(roomId: String, message: FcrChatRoomReceiveTextMessage) {
            Trace.Builder("onTextMessageReceived").page(FcrTestRoomControlFragment.PAGE).info("roomId:$roomId message:$message").commit()
        }

//        override fun onImageMessageReceived(roomId: String, message: FcrChatRoomReceiveImageMessage) {
//            Trace.Builder("onImageMessageReceived").page(FcrTestRoomControlFragment.PAGE).info("roomId:$roomId message:$message").commit()
//        }

        override fun onCustomMessageReceived(roomId: String, message: FcrChatRoomReceiveCustomMessage) {
            Trace.Builder("onCustomMessageReceived").page(FcrTestRoomControlFragment.PAGE).info("roomId:$roomId message:$message").commit()
        }

        override fun onErrorOccurred(roomId: String, code: Int) {
            Trace.Builder("onErrorOccurred").page(FcrTestRoomControlFragment.PAGE).info("roomId:$roomId code:$code").commit()
        }

        override fun onAnnouncementUpdated(roomId: String, announcement: String) {
            Trace.Builder("onAnnouncementUpdated").page(FcrTestRoomControlFragment.PAGE).info("roomId:$roomId announcement:$announcement").commit()
        }

        override fun onAnnouncementDeleted(roomId: String) {
            Trace.Builder("onAnnouncementDeleted").page(FcrTestRoomControlFragment.PAGE).info("roomId:$roomId").commit()
        }

        override fun onLocalMutedStateChanged(muted: Boolean) {
            Trace.Builder("onLocalMutedStateChanged").page(FcrTestRoomControlFragment.PAGE).info("roomId:$muted state:$muted").commit()
        }

    }
    private val handler = Handler(Looper.getMainLooper())
    private val roomId: String
        get() {
            return arguments?.getString("roomId") ?: ""
        }
    override fun initData() {
        super.initData()
        engine.getRoomControl(roomId)?.getChatRoomControl()?.addObserver(observer)
        modules.add(ApiInfo("getConnectionState") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val state = engine.getRoomControl(roomId)?.getChatRoomControl()?.getConnectionState()
            Trace.Builder(api).page(PAGE).info("getConnectionState state:$state").commit()
        })

        modules.add(ApiInfo("FcrChatRoomSendBaseMessage") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val users: List<FcrUserInfo> = engine.getRoomControl(roomId)?.getUserControl()?.getUserList() ?: emptyList()
            val items: Array<String> = arrayOf("room") + users.map { it.userName }.toTypedArray()

            ListFragmentDialog().apply {
                arguments = Bundle().apply {
                    putStringArray(ListFragmentDialog.ITEMS_KEY, items)
                }
                setOnItemSelectedListener { index, _ ->

                    val message =
                        when (index) {
                            0 -> packSendTextMessage("test message")
                            else -> {
                                packSendTextMessage(
                                    "test message",
                                    arrayListOf((users[index - 1].let { userInfo ->
                                        FcrChatRoomUserInfo(
                                            userInfo.userId,
                                            userInfo.userId,
                                            userInfo.userName,
                                            userInfo.role.index
                                        )
                                    }))
                                )
                            }
                        }
                    engine.getRoomControl(roomId)?.getChatRoomControl()?.sendMessage(message, object : FcrValueCallback<FcrChatRoomReceiveBaseMessage> {
                        override fun onSuccess(result: FcrChatRoomReceiveBaseMessage) {
                            Trace.Builder("FcrChatRoomSendBaseMessage").page(PAGE).info("FcrChatRoomSendBaseMessage succeed to send msg, id:${result.id}").commit()
                        }

                        override fun onFailure(error: Throwable, errorCode: Int) {
                            Trace.Builder("FcrChatRoomSendBaseMessage").page(PAGE).info("FcrChatRoomSendBaseMessage error:${error}").commit()
                        }
                    })
                }
            }.show(childFragmentManager, "Send Message")
        })
    }

    private fun packSendTextMessage(
        msg: String,
        to: ArrayList<FcrChatRoomUserInfo>? = null
    ): FcrChatRoomSendTextMessage {
        return FcrChatRoomSendTextMessage(
            to = to,
            content = msg
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        engine.getRoomControl(roomId)?.getChatRoomControl()?.removeObserver(observer)
    }
}