package com.derry.kt_coroutines.t82;

import androidx.annotation.NonNull;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class JavaTest {

    public static void main(String[] args) {

        Object funResult = DerrySuspendTypeKt.action1Suspend(5236475.6, new Continuation<String>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {
                System.out.println("action1Suspend挂起函数执行完成了 resume结果是:" + o);
            }
        });

        System.out.println("funResult:" + funResult);

        MainActivity4Kt.suspendFunction(new Continuation<Unit>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {

            }
        });
    }
}
