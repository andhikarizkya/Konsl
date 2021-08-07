package com.example.konsl.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
class Consultation(
        var id: String,
        var userName: String,
        var userId: String,
        var problem: String? = null,
        var effort: String? = null,
        var obstacle: String? = null,
        var status: String? = null,
        var timeRequest: String? = null,
        var genderRequest: String? = null,
        var timeAccepted: Timestamp? = null,
        var counselorId: String? = null,
        var counselorName: String? = null,
        var createdAt: Timestamp = Timestamp.now()
): Parcelable