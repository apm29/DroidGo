package io.github.apm29.core.utils


/**
 * 事件,data获取一次后不可再次获取
 */
data class Event<T>(
    private val data: T
){
    var consumed = false

    fun getDataIfNotConsumed(callback:(T)->Unit){
        if (!consumed){
            callback(data)
            consumed = true
        }
    }

    fun getDataIfNotConsumed():T?{
        return if (!consumed){
            consumed = true
            data
        } else {
            null
        }
    }

    /**
     * 需要重用的时候返回data
     */
    fun peek():T {
        return data
    }

    private val classesThatHandledTheEvent = HashSet<String>(0)

    /**
     * 对应class未使用时返回data,否则为null
     * Returns the content and prevents its use again from the given class.
     */
    fun getDataIfNotConsumed(classThatWantToUseEvent: Any): T? {
        val canonicalName = classThatWantToUseEvent::javaClass.get().canonicalName

        canonicalName?.let {
            return if (!classesThatHandledTheEvent.contains(canonicalName)) {
                classesThatHandledTheEvent.add(canonicalName)
                data
            } else {
                null
            }
        } ?: return null
    }


}

