package io.github.apm29.driodgo.model.artifact.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MiniImage(
    @SerializedName("default")
    val default: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(default)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MiniImage> {
        override fun createFromParcel(parcel: Parcel): MiniImage {
            return MiniImage(parcel)
        }

        override fun newArray(size: Int): Array<MiniImage?> {
            return arrayOfNulls(size)
        }
    }
}