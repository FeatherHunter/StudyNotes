package com.derry.kt_coroutines.t94

// TODO OKHttp的同步与异步  99.异步的规范

// TODO 异步:
/**
OkHttpClient okHttpClient = new OkHttpClient();

Request request = new Request.Builder()
    .url("https://api.github.com/")
    .build();

okHttpClient.newCall(request).enqueue(new Callback() {
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        System.out.println("请求成功");
    }

    @Override
    public void onFailure(Call call, IOException e) {
        System.out.println("请求失败");
    }
});
 */

// TODO 同步：
/**
object MainAppDerry {

    private val TAG = javaClass.simpleName

    private val okHttpClient = createClient()

    @JvmStatic
    fun test() {
        val request = Request.Builder().get().url("http://www.baidu.com").build()

        try {
            // thread {
                val call = okHttpClient.newCall(request)
                val resp = call.execute()
                Log.d(TAG, "resp==" + resp.body?.string())
            // }
        } catch (e: Exception) {
                 e.printStackTrace()
         }
    }
}
 */
