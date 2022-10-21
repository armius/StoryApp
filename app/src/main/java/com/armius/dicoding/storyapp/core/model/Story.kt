package com.armius.dicoding.storyapp.core.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val name: String?,
    val description: String?,
    val photoUrl: String?,
    val createdAt: String?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    )

    companion object : Parceler<Story> {
        override fun Story.write(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(description)
            parcel.writeString(photoUrl)
            parcel.writeString(createdAt)
        }

        override fun create(parcel: Parcel): Story {
            return Story(parcel)
        }
    }
}
