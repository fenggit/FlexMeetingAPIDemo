package io.agora.educore.test.tracer

object Tracer {
    private var tracer: ITracer? = null
    fun commit(trace: Trace) {
        requireNotNull(tracer) { "调用inject进行初始化 " }
        tracer!!.commit(trace)
    }

    fun inject(tracer: ITracer) {
        Tracer.tracer = tracer
    }
}