package io.github.apm29.driodgo.model.artifact.bean

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo

data class CardReference(
    @ColumnInfo
    val card_id: Int?,
    @ColumnInfo
    val ref_type:String?,
    @ColumnInfo
    val count :Int?,
    @ColumnInfo
    var image:String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(card_id)
        parcel.writeString(ref_type)
        parcel.writeValue(count)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardReference> {
        override fun createFromParcel(parcel: Parcel): CardReference {
            return CardReference(parcel)
        }

        override fun newArray(size: Int): Array<CardReference?> {
            return arrayOfNulls(size)
        }
    }
}