package com.hotbitmapgg.bilibili.network.api

import com.hotbitmapgg.bilibili.entity.auth.OauthKey
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

/**
 * 登录/认证
 * Created by hp on 2018/1/9.
 */
interface AuthService {
    @POST("/api/oauth/getKey")
    @FormUrlEncoded
    fun getKey(@FieldMap param: Map<String, String>): Observable<OauthKey>
}