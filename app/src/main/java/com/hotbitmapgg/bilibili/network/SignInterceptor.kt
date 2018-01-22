package com.hotbitmapgg.bilibili.network

import com.hotbitmapgg.bilibili.network.auxiliary.ApiConstants
import okhttp3.FormBody
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
            val param = HashMap<String, String>(url.queryParameterNames().size + 1)
            url.queryParameterNames()
                    .forEach {
                        param[it] = url.queryParameter(it)!!
                    }
            val newRequest = request.newBuilder().url(url.newBuilder().addQueryParameter("sign", ApiConstants.calculateSign(param)).build()).build()
            return chain.proceed(newRequest)
        } else {
            val body = request.body()
            if (body is FormBody) {
                //仅限form签名
                val param = HashMap<String, String>(body.size() + 1)
                body.size().minus(1).downTo(0).forEach {
                    param[body.encodedName(it)] = body.encodedValue(it)
                }
                val newBody = FormBody.Builder()
                param.forEach {
                    newBody.addEncoded(it.key, it.value)
                }
                newBody.addEncoded("sign", ApiConstants.calculateSign(param))
                return chain.proceed(request.newBuilder().method(request.method(), newBody.build()).build())
            }
        }

        return chain.proceed(request)
    }
}