import LeetCode.P405_数字转换为十六进制数
import kotlin.reflect.KProperty
import kotlin.system.measureTimeMillis


/**
 *
 **/

class LazyInitDelegate<T>(private var initializer: (() -> T)?) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return initializer?.invoke() ?: throw IllegalStateException("Property not initialized")
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: (() -> T)) {
        initializer = value
    }
}

class Example {
    val lazyProperty: String by LazyInitDelegate {
        // 在第一次访问属性时执行初始化逻辑
        println("Initializing lazy property")
        "Lazy Initialized"
    }
    val name:String
        get() {
            println("初始化")
            return "WCH"
        }
}

fun main() {
    val example = Example()
    println("Hello")
    println(example.lazyProperty) // 输出：Initializing lazy property \n Lazy Initialized

    println(P405_数字转换为十六进制数.toHex(1000))

//    val deferred = async{
//        println()
//    }
//    val time =  measureTimeMillis {
//        deferred.await()
//    }
}