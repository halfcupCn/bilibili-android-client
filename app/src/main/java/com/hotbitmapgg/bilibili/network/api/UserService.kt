package com.hotbitmapgg.bilibili.network.api

import com.hotbitmapgg.bilibili.entity.user.UserCoinsInfo
import com.hotbitmapgg.bilibili.entity.user.UserContributeInfo
import com.hotbitmapgg.bilibili.entity.user.UserPlayGameInfo

import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by hcc on 2016/10/12 22:40
 * 100332338@qq.com
 *
 *
 * 用户相关api
 */
interface UserService {
    /**
     * 用户所玩游戏
     */
    @GET("ajax/game/GetLastPlay")
    fun getUserPlayGames(@Query("mid") mid: Int): Observable<UserPlayGameInfo>

    /**
     * 用户投币视频
     */
    @GET("ajax/member/getCoinVideos")
    fun getUserCoinVideos(@Query("mid") mid: Int): Observable<UserCoinsInfo>

    /**
     * 用户投稿视频
     */
    @GET("ajax/member/getSubmitVideos")
    fun getUserContributeVideos(@Query("mid") mid: Int, @Query("page") page: Int, @Query("pagesize") pageSize: Int): Observable<UserContributeInfo>
}
