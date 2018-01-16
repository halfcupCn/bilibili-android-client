package com.hotbitmapgg.bilibili.network.auxiliary

import android.util.Base64
import java.math.BigInteger
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPublicKey
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

/**
 * Created by hcc on 2016/11/20 21:32
 * 100332338@qq.com
 *
 *
 * API常量类
 */
object ApiConstants {
    val BILI_GO_BASE_URL = "http://bilibili-service.daoapp.io/"
    val RANK_BASE_URL = "http://www.bilibili.com/"
    val APP_BASE_URL = "http://app.bilibili.com/"
    val LIVE_BASE_URL = "http://live.bilibili.com/"
    val API_BASE_URL = "http://api.bilibili.cn/"
    val BANGUMI_BASE_URL = "http://bangumi.bilibili.com/"
    val SEARCH_BASE_URL = "http://s.search.bilibili.com/"
    val ACCOUNT_BASE_URL = "https://account.bilibili.com/"
    val USER_BASE_URL = "http://space.bilibili.com/"
    val VIP_BASE_URL = "http://vip.bilibili.com/"
    val IM9_BASE_URL = "http://www.im9.com/"
    val AUTH_BASE_URL = "https://passport.bilibili.com"
    val COMMON_UA_STR = "OhMyBiliBili Android Client/2.1 (100332338@qq.com)"
    val APP_KEY = "1d8b6e7d45233436"
    val APP_SECRET: String = "560c52ccd288fed045859ed18bffd973"
    val HARDWARE_ID = "JxdyESFAJkcjEicQbBBsCTlbal5uX2Y"
    val SCALE = "xxhdpi"
    val VERSION = "5.15.0.515000"
    val BUILD = VERSION.substring(VERSION.lastIndexOf(".") + 1)
    val BUVID = "JxdyESFAJkcjEicQbBBsCTlbal5uX2Yinfoc"
    var mid: Int = 0

    private val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmm000ss", Locale.getDefault())

    //Display-ID 的值在未登录前为 Buvid-客户端启动时间, 在登录后为 mid-客户端启动时间
    fun calculateDisplayId(): String {
        return if (mid != 0) calculateDisplayId(mid) else String.format("%s-%d", BUVID, System.currentTimeMillis() / 1000)
    }

    fun calculateDisplayId(mid: Int): String {
        return String.format("%d-%d", mid, System.currentTimeMillis() / 1000)
    }

    fun calculateTraceId(): String {
        return simpleDateFormat.format(Date())
    }


    //排序 params 并计算 sign
    //传入值为 name1=value1 形式
    fun calculateSign(nameAndValues: List<String>): String {
        Collections.sort(nameAndValues)
        val encodedQuery = nameAndValues.joinToString { "&" }
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(buildString { encodedQuery + APP_SECRET }.toByteArray())
            val md5 = BigInteger(1, messageDigest.digest()).toString(16)
            //md5 不满 32 位时左边加 0
            return ("00000000000000000000000000000000" + md5).substring(md5.length)
        } catch (e: NoSuchAlgorithmException) {
            throw Error(e)
        }
    }

    //排序 params 并计算 sign
    //传入值为 name1=value1 形式
    fun calculateSign(nameAndValues: HashMap<String, Any>): String {
        val encodedQuery = nameAndValues.map { "$it.key=$it.value" }.joinToString { "&" }
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(buildString { encodedQuery + APP_SECRET }.toByteArray())
            val md5 = BigInteger(1, messageDigest.digest()).toString(16)
            //md5 不满 32 位时左边加 0
            return ("00000000000000000000000000000000" + md5).substring(md5.length)
        } catch (e: NoSuchAlgorithmException) {
            throw Error(e)
        }
    }

    //加密密码
    @Throws(InvalidKeyException::class)
    fun cipherPassword(password: String, hash: String, rsaPublicKey: RSAPublicKey): String {
        try {
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey)
            return String(Base64.encode(cipher.doFinal((hash + password).toByteArray()), Base64.NO_WRAP))
        } catch (e: NoSuchAlgorithmException) {
            throw Error(e)
        } catch (e: NoSuchPaddingException) {
            throw Error(e)
        } catch (e: IllegalBlockSizeException) {
            throw Error(e)
        } catch (e: BadPaddingException) {
            throw Error(e)
        }
    }
}
