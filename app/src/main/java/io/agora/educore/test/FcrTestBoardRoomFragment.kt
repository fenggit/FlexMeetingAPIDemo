package io.agora.educore.test

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.agora.agoracore.core2.bean.FcrConnectionState
import io.agora.agoracore.core2.bean.FcrUserInfo
import io.agora.agoracore.core2.control.board.FcrWhiteboardObserver
import io.agora.agoracore.core2.control.board.bean.FcrBoardInactiveReason
import io.agora.agoracore.core2.control.board.bean.FcrBoardLogType
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.educore.example.databinding.FragmentTestBoardBinding
import io.agora.educore.test.tracer.Trace

/**
 * author : qinwei@agora.io
 * date : 2024/10/11 18:24
 * description :
 */
class FcrTestBoardFragment : FcrTestBaseListFragment() {
    companion object {
        const val PAGE = "Board"
    }

    private lateinit var binding: FragmentTestBoardBinding
    private val boardObserver = object : FcrWhiteboardObserver() {
        override fun onConnectionStateUpdated(state: FcrConnectionState) {
            Trace.Builder("onConnectionStateUpdated").page(PAGE).info(state.toString()).commit()
            if (state == FcrConnectionState.CONNECTED) {
                engine.getRoomControl(roomId)?.getBoardControl()?.getMainWindow()?.getContentView()?.let {
                    engine.getRoomControl(roomId)?.getBoardControl()?.getBackgroundColor()?.apply {
                        try {
                            it.setBackgroundColor(Color.parseColor(this))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    (it.parent as? ViewGroup)?.removeAllViews()
                    binding.flBoard.removeAllViews()
                    binding.flBoard.addView(it)
                }
            } else if (state == FcrConnectionState.DISCONNECTED) {
                binding.flBoard.removeAllViews()
            }
        }

        override fun onActive(ownerId: String, operatorUser: FcrUserInfo?) {
            Trace.Builder("onActive").page(PAGE).info(ownerId + " " + operatorUser?.userId).commit()
        }

        override fun onInactive(reason: FcrBoardInactiveReason, operatorUser: FcrUserInfo?) {
            Trace.Builder("onInactive").page(PAGE).info("$reason " + operatorUser?.userId).commit()
        }

        override fun onBackgroundColorUpdated(color: String, operatorUser: FcrUserInfo?) {
            Trace.Builder("onBackgroundColorUpdated").page(PAGE).info("$color " + operatorUser?.userId).commit()
            engine.getRoomControl(roomId)?.getBoardControl()?.getMainWindow()?.getContentView()?.let {
                val co = Color.parseColor(color)
                it.setBackgroundColor(co)
            }

        }

        override fun onBoardLog(log: String, extra: String?, type: FcrBoardLogType) {}
        override fun onBoardError(code: Int, message: String) {
            Trace.Builder("onBoardError").page(PAGE).info("$code $message").commit()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentTestBoardBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    private val roomId: String
        get() {
            return arguments?.getString("roomId") ?: ""
        }

    override fun initData() {
        super.initData()
        val boardControl = engine.getRoomControl(roomId)?.getBoardControl() ?: return
        boardControl.addObserver(boardObserver)
        modules.add(ApiInfo("active") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            boardControl.active(object : FcrCallback<Void?> {
                override fun onSuccess(res: Void?) {
                    Trace.Builder(api).page(PAGE).info("onSuccess").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("inactive") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            boardControl.inactive(object : FcrCallback<Void?> {
                override fun onSuccess(res: Void?) {
                    Trace.Builder(api).page(PAGE).info("onSuccess").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("open") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            boardControl.open()
        })
        modules.add(ApiInfo("close") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            boardControl.close()
        })
        modules.add(ApiInfo("setBackgroundColor") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            boardControl.setBackgroundColor("#000000", object : FcrCallback<Void?> {
                override fun onSuccess(res: Void?) {
                    Trace.Builder(api).page(PAGE).info("onSuccess").commit()
                }

                override fun onFailure(error: FcrError) {
                    Trace.Builder(api).page(PAGE).info("error:$error").commit()
                }
            })
        })
        modules.add(ApiInfo("getActivity") {
            Trace.Builder(api).page(PAGE).info("${boardControl.getActivity()}").commit()
        })
        modules.add(ApiInfo("getOwnerId") {
            Trace.Builder(api).page(PAGE).info("${boardControl.getOwnerId()}").commit()
        })
        modules.add(ApiInfo("getBackgroundColor") {
            Trace.Builder(api).page(PAGE).info(boardControl.getBackgroundColor()).commit()
        })
        modules.add(ApiInfo("getConnectionState") {
            Trace.Builder(api).page(PAGE).info("${boardControl.getConnectionState()}").commit()
        })
        modules.add(ApiInfo("getMainWindow") {
            Trace.Builder(api).page(PAGE).info("${boardControl.getMainWindow()}").commit()
        })
    }

    override fun getPage(): String {
        return PAGE
    }

    override fun onDestroy() {
        super.onDestroy()
        engine.getRoomControl(roomId)?.getBoardControl()?.removeObserver(boardObserver)
    }
}