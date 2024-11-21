package io.agora.educore.test.tracer


interface ITracer {
    fun commit(trace: Trace)
}