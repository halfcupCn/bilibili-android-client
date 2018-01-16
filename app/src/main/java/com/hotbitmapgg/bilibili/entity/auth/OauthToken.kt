package com.hotbitmapgg.bilibili.entity.auth

import android.os.Parcel
import android.os.Parcelable

/**
 * auth token
 * Created by hp on 2018/1/14.
 */
class OauthToken(val ts: Long, val code: Int, val data: RealToken) : Parcelable {
    class RealToken(private val mid: Long, private val access_token: String, private val refresh_token: String, private val expires_in: Long) : Parcelable {
        constructor(source: Parcel) : this(
                source.readLong(),
                source.readString(),
                source.readString(),
                source.readLong()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeLong(mid)
            writeString(access_token)
            writeString(refresh_token)
            writeLong(expires_in)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<RealToken> = object : Parcelable.Creator<RealToken> {
                override fun createFromParcel(source: Parcel): RealToken = RealToken(source)
                override fun newArray(size: Int): Array<RealToken?> = arrayOfNulls(size)
            }
        }
    }

    constructor(source: Parcel) : this(
            source.readLong(),
            source.readInt(),
            source.readParcelable<RealToken>(RealToken::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(ts)
        writeInt(code)
        writeParcelable(data, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OauthToken> = object : Parcelable.Creator<OauthToken> {
            override fun createFromParcel(source: Parcel): OauthToken = OauthToken(source)
            override fun newArray(size: Int): Array<OauthToken?> = arrayOfNulls(size)
        }
    }
}