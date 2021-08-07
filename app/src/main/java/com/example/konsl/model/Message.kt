package com.example.konsl.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
class Message(
    var id: String,
    var message: String? = "",
    var senderId: String,
    var createdAt: Timestamp
): Parcelable