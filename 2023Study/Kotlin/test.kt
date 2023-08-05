class LoggingDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val value = property.getter.call()
        println("Property ${property.name} is accessed, value: $value")
        return value
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        println("Property ${property.name} is set with value: $value")
        property.setter.call(thisRef, value)
    }
}

class Example {
    var property: String by LoggingDelegate()
}

fun main() {
    val example = Example()
    example.property = "New value" // 输出：Property property is set with value: New value
    println(example.property) // 输出：Property property is accessed, value: New value \n New value
}
