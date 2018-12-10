package io.github.apm29.driodgo.model.artifact.bean

import android.os.Parcel
import android.os.Parcelable

data class InGameImage(
    val url: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InGameImage> {
        override fun createFromParcel(parcel: Parcel): InGameImage {
            return InGameImage(parcel)
        }

        override fun newArray(size: Int): Array<InGameImage?> {
            return arrayOfNulls(size)
        }
    }
}