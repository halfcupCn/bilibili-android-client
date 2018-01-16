package com.hotbitmapgg.bilibili.network

import com.hotbitmapgg.bilibili.network.auxiliary.ApiConstants
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

/**
 * 请求参数生成sign，自动添加到参数表
 * Created by hp on 2018/1/11.
 */
class SignInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method().toLowerCase() == "get") {
            //get直接拼接参数
            val url = request.url()
            val param = HashMap<String, Any>(url.queryParameterNames().size + 1)
            url.queryParameterNames()
                    .sorted()
                    .forEach {
                        param.put(it, url.queryParameter(it)!!)
                    }
            val newRequest = request.newBuilder().url(url.newBuilder().addQueryParameter("sign", ApiConstants.calculateSign(param)).build()).build()
            return chain.proceed(newRequest)
        } else {
            val body = request.body()
        }

        return chain.proceed(request)
    }
}