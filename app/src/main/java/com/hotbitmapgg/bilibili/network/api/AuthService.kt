package com.hotbitmapgg.bilibili.network.api

import com.hotbitmapgg.bilibili.entity.auth.OauthKey
import com.hotbitmapgg.bilibili.entity.auth.OauthToken
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import rx.Observable

/**
 * 登录/认证
 * Created by hp on 2018/1/9.
 */
interface AuthService {
    @POST("/api/oauth2/getKey")
    @FormUrlEncoded
    fun getKey(@FieldMap param: Map<String,@JvmSuppressWildcards Any>): Observable<OauthKey>

    @POST("/api/oauth2/login")
    @FormUrlEncoded
    fun login(@FieldMap param: Map<String,@JvmSuppressWildcards Any>): Observable<OauthToken>
}