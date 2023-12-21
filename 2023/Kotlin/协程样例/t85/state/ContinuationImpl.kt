package com.derry.kt_coroutines.t85.state

import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

/** TODO >>>>>>>>>>>>>>>>>>>>>>>>  协程背后状态机原理  >>>>>>>>>>>>>>>>>>>>>>>>
 * Kotlin 源码中的 ContinuationImpl 无法直接访问，
 * 为防止报错，将 ContinuationImpl 拷贝了部分代码出来
 */
abstract class ContinuationImpl(
        completion: Continuation<Any?>?,
        private val _context: CoroutineContext?
) : Continuation<Any?> {

    constructor(completion: Continuation<Any?>?) : this(completion, completion?.context)

    public override val context: CoroutineContext
        get() = _context!!

    @Transient
    private var intercepted: Continuation<Any?>? = null

    public fun intercepted(): Continuation<Any?> =
            intercepted
                    ?: (context[ContinuationInterceptor]?.interceptContinuation(this) ?: this)
                            .also { intercepted = it }

    public final override fun resumeWith(result: Result<Any?>) { }

    abstract fun invokeSuspend(result: Result<Any?>): Any? // 抽象方法定义

}

// 枚举类定义
enum class CoroutineSingletons { COROUTINE_SUSPENDED, UNDECIDED, RESUMED }
