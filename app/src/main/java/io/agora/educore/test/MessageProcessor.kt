package io.agora.educore.test

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker

/**
 * author : chenbinhang@agora.io
 * date : 2024/10/14
 * description :
 */
class MessageProcessor private constructor() {
    companion object {
        private var instance: MessageProcessor? = null
        fun getInstance(): MessageProcessor {
            return instance ?: synchronized(this) {
                instance ?: MessageProcessor().also { instance = it }
            }
        }
    }

    private val channel = Channel<() -> Unit>(Channel.UNLIMITED) // 创建一个可以接收消息的Channel
    private var job: Job? = null

    init {
        startProcessing() // 自动启动消息处理
    }

    // 向队列中快速put消息
    fun putAction(message: () -> Unit) {
        channel.trySend(message) // 非阻塞发送消息
    }

    // 启动后台协程，每隔10秒处理一次最后一条消息
    @OptIn(ObsoleteCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private fun startProcessing() {
        job = CoroutineScope(Dispatchers.IO).launch {
            val tickerChannel = ticker(delayMillis = 10_000) // 每隔10秒发一次tick
            var lastMessage: (() -> Unit)? = null
            for (tick in tickerChannel) {
                // 从channel中获取最后一条消息
                while (!channel.isEmpty) {
                    lastMessage = channel.receive() // 每次覆盖，只保留最后一条
                }
                withContext(Dispatchers.Main) {
                    lastMessage?.invoke()
                }
            }
        }
    }

    // 停止处理
    fun stopProcessing() {
        job?.cancel()
        channel.close()
    }
}
