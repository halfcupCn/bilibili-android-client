package com.hotbitmapgg.bilibili.entity.auth

import android.os.Parcel
import android.os.Parcelable

/**
 * auth key 实体
 * Created by hp on 2018/1/9.
 */
class OauthKey(val ts: Long, val code: Int, val data: RealKey) : Parcelable {
    class RealKey(val hash: String, val key: String) : Parcelable {

        constructor(source: Parcel) : this(source.readString(), source.readString())

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            dest.writeString(hash)
            dest.writeString(key)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<RealKey> = object : Parcelable.Creator<RealKey> {
                override fun createFromParcel(source: Parcel): RealKey = RealKey(source)
                override fun newArray(size: Int): Array<RealKey?> = arrayOfNulls(size)
            }
        }
    }

    constructor(source: Parcel) : this(source.readLong(), source.readInt(), source.readParcelable(RealKey::class.java.classLoader))

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        dest.writeLong(ts)
        dest.writeInt(code)
        dest.writeParcelable(data, flags)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OauthKey> = object : Parcelable.Creator<OauthKey> {
            override fun createFromParcel(source: Parcel): OauthKey = OauthKey(source)
            override fun newArray(size: Int): Array<OauthKey?> = arrayOfNulls(size)
        }
    }
}