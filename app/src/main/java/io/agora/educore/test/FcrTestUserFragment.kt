package io.agora.educore.test

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import io.agora.agoracore.core2.FcrCoreEngine
import io.agora.agoracore.core2.bean.FcrUserInfo
import io.agora.agoracore.core2.bean.FcrUserKickOutType
import io.agora.agoracore.core2.bean.FcrUserPageParams
import io.agora.agoracore.core2.bean.FcrUserPageResponse
import io.agora.agoracore.core2.bean.FcrUserRole
import io.agora.core.common.http.bean.HttpBaseRes
import io.agora.core.common.obs.FcrCallback
import io.agora.core.common.obs.FcrError
import io.agora.core.common.utils.GsonUtil
import io.agora.educore.test.tracer.Trace
import io.agora.educore.test.view.ListFragmentDialog

/**
 * author : chenbinhang@agora.io
 * date : 2024/10/14
 * description :
 */
class FcrTestUserFragment : FcrTestBaseListFragment() {
    companion object {
        const val PAGE = "User"
        fun selectUser(
            roomId: String,
            engine: FcrCoreEngine,
            childFragmentManager: FragmentManager,
            tag: String,
            action: (user: FcrUserInfo) -> Unit
        ) {
            engine.getRoomControl(roomId)?.getUserControl()?.getUserList()?.let { users ->
                val list = users.map { it.userName }.toTypedArray()
                val dialog = ListFragmentDialog()
                val args = Bundle()
                args.putStringArray(ListFragmentDialog.ITEMS_KEY, list)
                dialog.arguments = args
                dialog.setOnItemSelectedListener { index, _ ->
                    val user = engine.getRoomControl(roomId)?.getUserControl()?.getUser(users[index].userId)
                    if (user == null) {
                        Trace.Builder("").page(FcrTestUserFragment.PAGE).info("getUser user is null").commit()
                        dialog.dismiss()
                        return@setOnItemSelectedListener
                    }
                    action(user)
                }.show(childFragmentManager, tag)
            }
        }
    }

    private val roomId: String
        get() {
            return arguments?.getString("roomId") ?: ""
        }

    override fun initData() {
        childFragmentManager
        super.initData()
        modules.add(ApiInfo("getLocalUser") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val user = engine.getRoomControl(roomId)?.getUserControl()?.getLocalUser()
            Trace.Builder(api).page(PAGE).info("getUserControl user:$user").commit()
        })

        modules.add(ApiInfo("getUsers") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val users = engine.getRoomControl(roomId)?.getUserControl()?.getUsers()
            users?.entries?.forEach {
                Trace.Builder(api).page(PAGE).info("getUsers user:${it.key} value:${it.value}").commit()
            }
        })

        modules.add(ApiInfo("getUserList") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val users = engine.getRoomControl(roomId)?.getUserControl()?.getUserList()
            users?.forEach {
                Trace.Builder(api).page(PAGE).info("getUserList user:${it}").commit()
            }
        })

        modules.add(ApiInfo("getUser") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            selectUser(roomId, engine, childFragmentManager, "getUser") { user ->
                Trace.Builder(api).page(PAGE).info("getUser user:${user}").commit()
            }
        })

        modules.add(ApiInfo("getAllUserCount") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            val userCount = engine.getRoomControl(roomId)?.getUserControl()?.getAllUserCount()
            Trace.Builder(api).page(PAGE).info("getAllUserCount :${userCount}").commit()
        })

        modules.add(ApiInfo("fetchUserList") {
            val params = FcrUserPageParams()
            Trace.Builder(api).page(PAGE).info("call").commit()
            val userCount = engine.getRoomControl(roomId)?.getUserControl()
                ?.fetchUserList(params, object : FcrCallback<FcrUserPageResponse> {
                    override fun onSuccess(res: FcrUserPageResponse?) {
                        Trace.Builder(api).page(PAGE).info("fetchUserList :${res?.userCount}").commit()
                        res?.userList?.forEach {
                            Trace.Builder(api).page(PAGE).info("fetchUserList user:${it}").commit()
                        }
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("fetchUserList error:${error}").commit()
                    }
                }
                )
            Trace.Builder(api).page(PAGE).info("getAllUserCount :${userCount}").commit()
        })

        modules.add(ApiInfo("updateUserProperties") {
            selectUser(roomId, engine, childFragmentManager, "updateUserProperties") {
                val properties = mapOf("key" to 1)
                val cause = mapOf("key" to 1)
                Trace.Builder(api).page(PAGE).info("call").commit()
                engine.getRoomControl(roomId)?.getUserControl()?.updateUserProperties(properties, cause, it.userId, object : FcrCallback<Any> {
                    override fun onSuccess(res: Any?) {
                        Trace.Builder(api).page(PAGE).info("updateUserProperties true :${httpResToString(res)}").commit()
                    }

                        override fun onFailure(error: FcrError) {
                            super.onFailure(error)
                            Trace.Builder(api).page(PAGE).info("updateUserProperties error:${error}").commit()
                        }
                    })
            }

        })

        modules.add(ApiInfo("updateIncrementUserProperties") {
            selectUser(roomId,engine, childFragmentManager, "updateIncrementUserProperties") {
                val properties = mapOf("key" to 2)
                val cause = mapOf("key" to 2)
                Trace.Builder(api).page(PAGE).info("call").commit()
                engine.getRoomControl(roomId)?.getUserControl()
                    ?.updateIncrementUserProperties(properties, cause, it.userId, object : FcrCallback<Any> {
                        override fun onSuccess(res: Any?) {
                            Trace.Builder(api).page(PAGE).info("updateIncrementUserProperties true :${httpResToString(res)}").commit()
                        }

                        override fun onFailure(error: FcrError) {
                            super.onFailure(error)
                            Trace.Builder(api).page(PAGE).info("updateIncrementUserProperties error:${error}").commit()
                        }
                    })
            }
        })

        modules.add(ApiInfo("deleteUserProperties") {
            selectUser(roomId, engine, childFragmentManager, "deleteUserProperties") {
                val keyPaths = listOf("key")
                val cause = mapOf("key" to 3)
                Trace.Builder(api).page(PAGE).info("call").commit()
                engine.getRoomControl(roomId)?.getUserControl()
                    ?.deleteUserProperties(it.userId, keyPaths, cause, object : FcrCallback<Any> {
                        override fun onSuccess(res: Any?) {
                            Trace.Builder(api).page(PAGE).info("deleteUserProperties true :${httpResToString(res)}").commit()
                        }

                        override fun onFailure(error: FcrError) {
                            super.onFailure(error)
                            Trace.Builder(api).page(PAGE).info("deleteUserProperties error:${error}").commit()
                        }
                    })
            }
        })

        modules.add(ApiInfo("getUserPropertiesByUserId") {
            selectUser(roomId, engine, childFragmentManager, "getUserPropertiesByUserId") { user ->
                Trace.Builder(api).page(PAGE).info("call").commit()
                val propertiesMap = engine.getRoomControl(roomId)?.getUserControl()?.getUserPropertiesByUserId(user.userId)
                Trace.Builder(api).page(PAGE).info("getUserPropertiesByUserId :$propertiesMap").commit()
                propertiesMap?.entries?.forEach {
                    Trace.Builder(api).page(PAGE).info("getUserPropertiesByUserId :${it.key} value:${it.value}")
                        .commit()
                }
            }
        })


        modules.add(ApiInfo("getUserPropertiesByKeyPath") {
            selectUser(roomId, engine, childFragmentManager, "getUserPropertiesByKeyPath") { user ->
                Trace.Builder(api).page(PAGE).info("call").commit()
                val keyPath = "key"
                val any = engine.getRoomControl(roomId)?.getUserControl()?.getUserPropertiesByKeyPath(keyPath, user.userId)
                Trace.Builder(api).page(PAGE).info("getUserPropertiesByKeyPath :${any}").commit()
            }
        })

        modules.add(ApiInfo("updateRemoteUserRole") {
            selectUser(roomId, engine, childFragmentManager, "updateRemoteUserRole") {
                ListFragmentDialog().apply {
                    arguments = Bundle().apply {
                        putStringArray(
                            ListFragmentDialog.ITEMS_KEY,
                            listOf("HOST", "CO_HOST", "PARTICIPANT").toTypedArray()
                        )
                    }
                    setOnItemSelectedListener { index, _ ->
                        val role = when (index) {
                            0 -> FcrUserRole.HOST
                            1 -> FcrUserRole.COHOST
                            else -> FcrUserRole.PARTICIPANT
                        }
                        Trace.Builder(api).page(PAGE).info("call").commit()
                        engine.getRoomControl()?.getUserControl()
                            ?.updateRemoteUserRole(it.userId, role, object : FcrCallback<Any> {
                                override fun onSuccess(res: Any?) {
                                    Trace.Builder(api).page(PAGE)
                                        .info("updateRemoteUserRole true:${GsonUtil.gson.toJson(res)}").commit()
                                }

                                override fun onFailure(error: FcrError) {
                                    super.onFailure(error)
                                    Trace.Builder(api).page(PAGE).info("updateRemoteUserRole error:${error}").commit()
                                }
                            })
                    }
                }.show(childFragmentManager, "UpdateRemoteUserRole")
            }
        })

        //revokeHost
        modules.add(ApiInfo("revokeHost") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            engine.getRoomControl(roomId)?.getUserControl()?.revokeHost(object : FcrCallback<Any> {
                override fun onSuccess(res: Any?) {
                    Trace.Builder(api).page(PAGE).info("revokeHost true:${httpResToString(res)}").commit()
                }

                override fun onFailure(error: FcrError) {
                    super.onFailure(error)
                    Trace.Builder(api).page(PAGE).info("revokeHost error:${error}").commit()
                }
            })
        })

        modules.add(ApiInfo("kickOut") {
            selectUser(roomId, engine, childFragmentManager, "kickOut") { user ->
                val type = FcrUserKickOutType.ONCE
                Trace.Builder(api).page(PAGE).info("call").commit()
                engine.getRoomControl(roomId)?.getUserControl()?.kickOut(user.userId, type, object : FcrCallback<Any> {
                    override fun onSuccess(res: Any?) {
                        Trace.Builder(api).page(PAGE).info("kickOut true:${httpResToString(res)}").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("kickOut error:${error}").commit()
                    }
                })
            }
        })

        modules.add(ApiInfo("mergeAudioStream") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            selectUser(roomId, engine, childFragmentManager, "mergeAudioStream") {
                engine.getRoomControl(roomId)?.getUserControl()?.mergeAudioStream(it.userId, object : FcrCallback<Any> {
                    override fun onSuccess(res: Any?) {
                        Trace.Builder(api).page(PAGE).info("mergeAudioStream true:${httpResToString(res)}").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("mergeAudioStream error:${error}").commit()
                    }
                })
            }
        })

        modules.add(ApiInfo("splitAudioStream") {
            Trace.Builder(api).page(PAGE).info("call").commit()
            selectUser(roomId, engine, childFragmentManager, "splitAudioStream") {
                engine.getRoomControl(roomId)?.getUserControl()?.splitAudioStream(it.userId, object : FcrCallback<Any> {
                    override fun onSuccess(res: Any?) {
                        Trace.Builder(api).page(PAGE).info("splitAudioStream true:${httpResToString(res)}").commit()
                    }

                    override fun onFailure(error: FcrError) {
                        super.onFailure(error)
                        Trace.Builder(api).page(PAGE).info("splitAudioStream error:${error}").commit()
                    }
                })
            }
        })
    }
}