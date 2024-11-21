package io.agora.educore.example.base

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * author : felix
 * date : 2024/3/7
 * description :
 */
open class BaseActivity : AppCompatActivity() {
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化加载框
        progressDialog = ProgressDialog(this).apply {
            setTitle("加载中")
            setMessage("请稍候...")
            setCancelable(true)
        }

    }


    fun showLoading() {
        runOnUiThread {
            progressDialog?.show()
        }
    }

    fun hideLoading() {
        runOnUiThread {
            progressDialog?.dismiss()
        }
    }


}