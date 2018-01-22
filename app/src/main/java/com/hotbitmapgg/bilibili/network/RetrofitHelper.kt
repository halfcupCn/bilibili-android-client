package com.hotbitmapgg.bilibili.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.hotbitmapgg.bilibili.BilibiliApp
import com.hotbitmapgg.bilibili.network.api.AccountService
import com.hotbitmapgg.bilibili.network.api.AuthService
import com.hotbitmapgg.bilibili.network.api.BangumiService
import com.hotbitmapgg.bilibili.network.api.BiliApiService
import com.hotbitmapgg.bilibili.network.api.BiliAppService
import com.hotbitmapgg.bilibili.network.api.BiliGoService
import com.hotbitmapgg.bilibili.network.api.Im9Service
import com.hotbitmapgg.bilibili.network.api.LiveService
import com.hotbitmapgg.bilibili.network.api.RankService
import com.hotbitmapgg.bilibili.network.api.SearchService
import com.hotbitmapgg.bilibili.network.api.UserService
import com.hotbitmapgg.bilibili.network.api.VipService
import com.hotbitmapgg.bilibili.network.auxiliary.ApiConstants
import com.hotbitmapgg.bilibili.utils.CommonUtil

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 *
 *
 * Retrofit帮助类
 */
object RetrofitHelper {
    private var mOkHttpClient: OkHttpClient? = null
    private var authClient: OkHttpClient? = null

    val liveAPI: LiveService
        get() = createApi(LiveService::class.java, ApiConstants.LIVE_BASE_URL)

    val biliAppAPI: BiliAppService
        get() = createApi(BiliAppService::class.java, ApiConstants.APP_BASE_URL)

    val biliAPI: BiliApiService
        get() = createApi(BiliApiService::class.java, ApiConstants.API_BASE_URL)

    val biliGoAPI: BiliGoService
        get() = createApi(BiliGoService::class.java, ApiConstants.BILI_GO_BASE_URL)

    val rankAPI: RankService
        get() = createApi(RankService::class.java, ApiConstants.RANK_BASE_URL)

    val userAPI: UserService
        get() = createApi(UserService::class.java, ApiConstants.USER_BASE_URL)

    val vipAPI: VipService
        get() = createApi(VipService::class.java, ApiConstants.VIP_BASE_URL)

    val bangumiAPI: BangumiService
        get() = createApi(BangumiService::class.java, ApiConstants.BANGUMI_BASE_URL)

    val searchAPI: SearchService
        get() = createApi(SearchService::class.java, ApiConstants.SEARCH_BASE_URL)

    val accountAPI: AccountService
        get() = createApi(AccountService::class.java, ApiConstants.ACCOUNT_BASE_URL)

    val im9API: Im9Service
        get() = createApi(Im9Service::class.java, ApiConstants.IM9_BASE_URL)

    val authAPI: AuthService
        get() = createAuthApi(AuthService::class.java, ApiConstants.AUTH_BASE_URL)

    init {
        initOkHttpClient()
        initAuthClient()
    }

    private fun initAuthClient() {
        if (authClient == null) {
            synchronized(RetrofitHelper::class.java) {
                if (authClient == null) {
                    //设置Http缓存
                    authClient = OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .addInterceptor(UserAgentInterceptor())
                            .addInterceptor(HeaderInterceptor())
                            .addInterceptor(SignInterceptor())
                            .build()
                }
            }
        }
    }

    /**
     * 根据传入的baseUrl，和api创建retrofit
     */
    private fun <T> createApi(clazz: Class<T>, baseUrl: String): T {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mOkHttpClient!!)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(clazz)
    }

    private fun <T> createAuthApi(clazz: Class<T>, baseUrl: String): T {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(authClient!!)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(clazz)
    }


    /**
     * 初始化OKHttpClient,设置缓存,设置超时时间,设置打印日志,设置UA拦截器
     */
    private fun initOkHttpClient() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        if (mOkHttpClient == null) {
            synchronized(RetrofitHelper::class.java) {
                if (mOkHttpClient == null) {
                    //设置Http缓存
                    val cache = Cache(File(BilibiliApp.getInstance().cacheDir, "HttpCache"), (1024 * 1024 * 10).toLong())
                    mOkHttpClient = OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(interceptor)
                            .addNetworkInterceptor(CacheInterceptor())
                            .addNetworkInterceptor(StethoInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .addInterceptor(UserAgentInterceptor())

                            .build()
                }
            }
        }
    }


    /**
     * 添加UA拦截器，B站请求API需要加上UA才能正常使用
     */
    private class UserAgentInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", ApiConstants.COMMON_UA_STR)
                    .build()
            return chain.proceed(requestWithUserAgent)
        }
    }

    /**
     * 为okhttp添加缓存，这里是考虑到服务器不支持缓存时，从而让okhttp支持缓存
     */
    private class CacheInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            // 有网络时 设置缓存超时时间1个小时
            val maxAge = 60 * 60
            // 无网络时，设置超时为1天
            val maxStale = 60 * 60 * 24
            var request = chain.request()
            if (CommonUtil.isNetworkAvailable(BilibiliApp.getInstance())) {
                //有网络时只从网络获取
                request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build()
            } else {
                //无网络时只从缓存中读取
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            }
            var response = chain.proceed(request)
            if (CommonUtil.isNetworkAvailable(BilibiliApp.getInstance())) {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build()
            } else {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build()
            }
            return response
        }
    }
}
