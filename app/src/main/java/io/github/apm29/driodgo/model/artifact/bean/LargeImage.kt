package io.github.apm29.driodgo.model.artifact.bean

import android.os.Parcel
import android.os.Parcelable

data class LargeImage(
    val schinese: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(schinese)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LargeImage> {
        override fun createFromParcel(parcel: Parcel): LargeImage {
            return LargeImage(parcel)
        }

        override fun newArray(size: Int): Array<LargeImage?> {
            return arrayOfNulls(size)
        }
    }
}