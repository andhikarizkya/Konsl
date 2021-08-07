package com.example.konsl.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Article (
    var id: String,
    var title: String,
    var thumbnailUrl: String = "https://picsum.photos/seed/picsum/300/200",
    var content: String? = null
): Parcelable