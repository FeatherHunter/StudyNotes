import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun main(args: Array<String>) {

}

/**
 * 自定义属性委托
 */

class Custom{
    operator fun getValue(owner: Owner, property:KProperty<*>):String{
        return "AAA"
    }

    operator fun setValue(owner: Owner, property:KProperty<*>,value :String){
        owner.data = value
    }
}

class Owner{
    var data:String by Custom() // 代理了哦
}

/**
 * 模板实现属性委托
 */
class Custom2(var data:String = "Default"):ReadWriteProperty<Owner, String>{
    override fun getValue(thisRef: Owner, property: KProperty<*>): String {
        return data
    }

    override fun setValue(thisRef: Owner, property: KProperty<*>, value: String) {
        thisRef.data = value
    }

}

// 在属性被委托对象初始化之前额外做一些的操作，例如数据验证、计算、日志记录等等
// 根据属性的不同需求，为不同属性，提供不同行为
class SmartProvider{
    operator fun provideDelegate(thisRef: Owner, property: KProperty<*>):ReadWriteProperty<Owner, String>{
        return if(property.name.isEmpty()){
            Custom2("empty")
        }else{
            Custom2("normal")
        }
    }
}
