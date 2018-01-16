package com.hotbitmapgg.bilibili.network

import com.hotbitmapgg.bilibili.network.auxiliary.ApiConstants
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 添加固定头
 * Created by hp on 2018/1/14.
 */
class HeaderInterceptor :Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
                .header("Display-ID", ApiConstants.calculateDisplayId())
                .header("Buvid",ApiConstants.BUVID)
                .header("Device-ID",ApiConstants.HARDWARE_ID)
                .header("User-Agent", "Mozilla/5.0 BiliDroid/5.15.0 (bbcallen@gmail.com)")
                .build()

        return chain.proceed(newRequest)
    }
}