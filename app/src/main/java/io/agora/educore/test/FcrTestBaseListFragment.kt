package io.agora.educore.test

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.divider.MaterialDividerItemDecoration
import io.agora.agoracore.core2.FcrCoreEngine
import io.agora.core.common.http.bean.HttpBaseRes
import io.agora.core.common.utils.GsonUtil
import io.agora.educore.example.R
import io.agora.educore.example.databinding.FragmentTestBaseListBinding
import io.agora.educore.test.tracer.Trace

/**
 * author : qinwei@agora.io
 * date : 2024/10/10 15:50
 * description :
 */
abstract class FcrTestBaseListFragment : Fragment(R.layout.fragment_test_base_list) {
    protected lateinit var mFcrTestUISceneViewModel: FcrTestUISceneViewModel
    protected lateinit var engine: FcrCoreEngine
    protected lateinit var adapter: Adapter<ViewHolder>
    protected val modules = ArrayList<ApiInfo>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFcrTestUISceneViewModel = ViewModelProvider(requireActivity())[FcrTestUISceneViewModel::class.java]
        engine = mFcrTestUISceneViewModel.engine!!
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                MaterialDividerItemDecoration.VERTICAL
            )
        )
        adapter = object : Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return Holder(layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false))
            }

            inner class Holder(itemView: View) : ViewHolder(itemView) {
                private lateinit var item: ApiInfo

                init {
                    itemView.setBackgroundResource(android.R.drawable.list_selector_background)
                    itemView.setOnClickListener {
                        item.call.invoke(item)
                    }
                }

                fun onBind(position: Int) {
                    item = modules[position]
                    (itemView as TextView).text = item.api
                }
            }

            override fun getItemCount() = modules.size

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                (holder as Holder).onBind(position)
            }
        }
        rv.adapter = adapter
        initData()
    }

    open fun initData() {
        modules.add(ApiInfo("TestAll") {
            Trace.Builder(api).page(getPage()).info("api size:${modules.size - 1}").commit()
            testAll()
        })
    }

    open fun testAll() {
        modules.forEach {
            if (it.api != "TestAll") {
                it.call.invoke(it)
            }
        }
    }

    /**
     * 这是一个添加测试项的模版
     */
    private fun mockAddApiInfo() {
        modules.add(ApiInfo("") {
            Trace.Builder(api).page("page").info("")
        })
    }

    open fun getPage(): String {
        return ""
    }

    fun httpResToString(res: Any?): String {
        var resBody = ""
        if (res is HttpBaseRes<*>) {
            resBody = GsonUtil.gson.toJson(res)
        }
        return resBody
    }
}

data class ApiInfo(val api: String, val call: ApiInfo.() -> Unit)