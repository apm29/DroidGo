package io.github.apm29.driodgo.model.artifact.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CardName(
    @SerializedName("schinese")
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

    companion object CREATOR : Parcelable.Creator<CardName> {
        override fun createFromParcel(parcel: Parcel): CardName {
            return CardName(parcel)
        }

        override fun newArray(size: Int): Array<CardName?> {
            return arrayOfNulls(size)
        }
    }
}