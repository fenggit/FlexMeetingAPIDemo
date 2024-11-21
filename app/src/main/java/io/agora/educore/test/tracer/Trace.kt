package io.agora.educore.test.tracer


class Trace {
    private var code: String? = null
    private var type: String? = null
    private var info: String? = null
    private var page: String? = null

    fun getCode(): String? {
        return code
    }

    fun getInfo(): String? {
        return info
    }

    fun getType(): String? {
        return type
    }

    fun getPage(): String? {
        return page
    }


    fun commit() {
        Tracer.commit(this)
    }

    override fun toString(): String {
        return "Trace(eventCode=$code, eventType=$type, eventName=$info, pageId=$page)"
    }

    class Builder constructor(
        private val code: String = ""
    ) {
        private var type: String? = null
        private var name: String? = null
        private var page: String? = null

        fun type(type: String?): Builder {
            this.type = type
            return this
        }

        fun info(name: String?): Builder {
            this.name = name
            return this
        }

        fun page(page: String): Builder {
            this.page = page
            return this
        }

        fun create(): Trace {
            val trace = Trace()
            trace.code = code
            trace.info = name
            trace.type = type
            trace.page = page
            return trace
        }

        fun commit() {
            create().commit()
        }
    }
}