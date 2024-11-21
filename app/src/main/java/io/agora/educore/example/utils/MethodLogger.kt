package io.agora.educore.example.utils

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

annotation class Trace

class TrackableInvocationHandler(private val target: Any) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        if (method.isAnnotationPresent(Trace::class.java)) {
            println("Entering method: ${method.name}")
            val executionTime = System.currentTimeMillis()
            val result = method.invoke(target, *(args ?: emptyArray()))
            println("Returning from method: ${method.name}")
            println("Execution time: ${System.currentTimeMillis() - executionTime} ms")
            return result
        }
        return method.invoke(target, *(args ?: emptyArray()))
    }
}

inline fun <reified T> T.trace(): T {
    val classLoader = this!!.javaClass.classLoader
    val handler = TrackableInvocationHandler(this)
    return Proxy.newProxyInstance(classLoader, this.javaClass.interfaces, handler) as T
}