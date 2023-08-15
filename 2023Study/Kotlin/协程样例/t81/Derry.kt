package com.derry.kt_coroutines.t81

import kotlinx.coroutines.*

// TODO 81-协程执行流程研究

// 第零端流程研究
/*fun main() {
    GlobalScope.launch {
        println("launch start")
        delay(2000L)
        println("launch end")
    }

    println("main 111") // 1
}*/
/*fun main() = runBlocking<Unit> {
    GlobalScope.launch {
        println("launch start") // 2
        delay(2000L)
        println("launch end")
    }

    println("main 111") // 1
}*/

// 第一段流程研究
/*
fun main() = runBlocking <Unit> {
    println("main 111") // 1     【执行顺序①】

    GlobalScope.launch { // 创建协程体 完了   【执行顺序②】
        println("launch start") // 3    【执行顺序⑤】
        delay(2000L)           //【执行顺序⑥】
        println("launch end") // 4     【执行顺序⑦】
    }

    println("main 222") // 2     【执行顺序③】

    Thread.sleep(2100L) // 42行会被阻塞，正在阻塞中...   【执行顺序④】

    println("main 333") // 5    【执行顺序⑧】
}*/

// 第二段流程研究
/*
fun main() = runBlocking <Unit> {
    println("main 111") // 1      【执行顺序①】

    GlobalScope.launch { // 创建协程体 完了  【执行顺序②】
        println("launch start")  // 3    【执行顺序⑤】
        delay(2000L)             // 【执行顺序⑥】
        println("launch end")  // 4     【执行顺序⑦】
    }

    println("main 222") // 2   【执行顺序③】

    delay(2100L)       // 【执行顺序④】

    println("main 333")  // 5   【执行顺序⑧】
}*/

// 第三段流程研究
/*fun main() = runBlocking<Unit> {
    println("main 111") // 1      【执行顺序①】

    GlobalScope.launch { // 创建协程体 完了   【执行顺序②】
        println("launch start") // 3    【执行顺序⑤】
        delay(2000L)          // 【执行顺序⑥】
        println("launch end") // 5     【执行顺序⑧】
    }

    println("main 222") // 2    【执行顺序③】

    GlobalScope.launch {  delay(2100L)  }  // 又 创建协程体 完了   【执行顺序④】

    println("main 333") // 4   【执行顺序⑦】
}*/

// 第四段流程研究
/*fun main() {
    println("main 111") // 1      【执行顺序①】

    runBlocking {               // 【执行顺序②】
        println("runBlocking start")  // 2  【执行顺序③】
        delay(2000L)            // 【执行顺序④】
        println("runBlocking end") // 3  【执行顺序⑤】
    }

    println("main 222") // 4   【执行顺序⑥】

    Thread.sleep(2100L)  // 【执行顺序⑦】

    println("main 333") // 5  【执行顺序⑧】
}*/

// 第五段流程研究
/*fun main() {
    println("main 111") // 1      【执行顺序①】

    runBlocking {
        println("runBlocking 1 start") // 2
        delay(2000L)
        println("runBlocking 1 end")  // 3
    }

    runBlocking {
        println("runBlocking 2 start") // 4
        delay(2000L)
        println("runBlocking 2 end") // 5
    }

    runBlocking {
        println("runBlocking 3 start") // 6
        delay(2000L)
        println("runBlocking 3 end") // 7
    }

    println("main 222") // 8

    val resultValue = runBlocking {
        delay(1000L)
        return@runBlocking "runBlocking run is successful"
    }

    println(resultValue) // 9
}*/

// 第六段流程研究
/*fun main() = runBlocking<Unit> {
    println("main 111") // 1      【执行顺序①】

    val deferred = async {   // 创建协程体 完了   【执行顺序②】
        println("async start") // 3   【执行顺序⑤】
        delay(1000L)       // 【执行顺序⑥】
        println("async end")  // 4   【执行顺序⑦】
        return@async "runBlocking run is successful"  // 【执行顺序⑧】
    }

    println("main 222")  // 2   【执行顺序③】

    val result = deferred.await() // 【执行顺序④】  【执行顺序⑨】
    println("async result:$result") // 5 【执行顺序10】
}*/


// 第七段流程研究
/*fun main() = runBlocking<Unit> {
    println("main 111") // 1      【执行顺序①】

    val deferred = async {   // 创建协程体 完了   【执行顺序②】
        println("async start") // 3   【执行顺序④】
        delay(1000L)       // 【执行顺序⑤】
        println("async end")  // 4   【执行顺序⑥】
        return@async "runBlocking run is successful"  // 【执行顺序七】
    }

    println("main 222")  // 2   【执行顺序③】
}*/

// 第八段流程研究
/*fun main() = runBlocking<Unit> {

    println("main 111") // 1      【执行顺序①】

    val job = launch {   // 创建协程体 完了   【执行顺序②】
        println("launch 1 start")  // 4    【执行顺序6】
        delay(1000L)            //【执行顺序7】
        println("launch 1 end")  // 6
    }

    println("main 222")  // 2  【执行顺序3】

    val job2 = launch {    // 创建协程体 完了   【执行顺序4】
        println("launch 2 start")  // 5  【执行顺序8】
        delay(1000L)           // 【执行顺序9】
        println("launch 2 end")  // 7   【执行顺序10】
    }

    println("main 333") // 3   【执行顺序5】
}*/

// 第九段流程研究
/*fun main() = runBlocking<Unit> {

    println("main 111") // 1      【执行顺序①】

    val job = launch {   // 创建协程体 完了   【执行顺序②】
        println("launch 1 start")  // 3   【执行顺序5】
        delay(1000L)            //【执行顺序6】
        println("launch 1 end")  // 4   【执行顺序7】
    }

    println("main 222")  // 2  【执行顺序3】

    job.join()  // 【执行顺序4】

    val job2 = launch(job) {    // 创建协程体 完了   【执行顺序8】
        println("launch 2 start")
        delay(1000L)
        println("launch 2 end")
    }

    println("main 333") // 5   【执行顺序9】
}*/






// 作业：不要运行，思考清楚后，再运行
/*fun main() = runBlocking<Unit> {

    println("main 111") // 1    【执行顺序1】

    val job = launch {     //【执行顺序2】
        println("launch 1 start") // 3  【执行顺序5】
        delay(1000L)  //【执行顺序6】
        println("launch 1 end") // 4   【执行顺序7】
    }

    println("main 222") // 2   【执行顺序3】

    job.join()  // 【执行顺序4】

    val job2 = launch(this.coroutineContext) {  //【执行顺序8】
        println("launch 2 start") // 6  【执行顺序10】
        delay(1000L)          // 【执行顺序11】
        println("launch 2 end") // 7  【执行顺序12】
    }

    println("main 333") // 5   【执行顺序9】
}*/

// 第十段流程研究
/*fun main() = runBlocking <Unit> {
    println("main 111") // 1    【执行顺序1】

    val job = launch {     // 协程体被创建【执行顺序2】
        println("launch 1 start")
        delay(1000L)
        println("launch 1 end")
    }

    println("main 222") // 2    【执行顺序3】

    Thread.sleep(500L) // 阻塞当前runBlocking作用域的协程    【执行顺序4】
    job.cancel()    //【执行顺序5】

    val job2 = launch(context = job) {  // 协程体被创建【执行顺序6】
        println("launch 1 start")
        delay(1000L)
        println("launch 1 end")
    }

    println("main 333") // 3  // 【执行顺序7】
}*/

// 第十一段流程研究
fun main() = runBlocking <Unit> {
    println("main 111") // 1     【执行顺序1】

    // 我又不是 runBlocking的子协程
    val job = GlobalScope.launch {     // 协程体被创建【执行顺序2】
        println("launch 1 start") // 3  【执行顺序5】
        delay(1000L)   // 【执行顺序6】
        println("launch 1 end")
    }

    println("main 222") // 2   【执行顺序3】

    Thread.sleep(500L) // 阻塞当前runBlocking作用域的协程   【执行顺序4】
    job.cancel() // 【执行顺序7】

    val job2 = launch(context = job) { // 协程体被创建【执行顺序8】
        println("launch 1 start")
        delay(1000L)
        println("launch 1 end")
    }

    println("main 333") // 4    【执行顺序9】
}


