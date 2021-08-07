package com.example.konsl.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
        var id: String,
        var name: String,
        var authId: String,
        var role: String,
        var gender: String,
        var hobby: String? = null,
        var address: String? = null,
        var birthPlace: String? = null,
        var birthDate: String? = null,
        var phoneNumber: String? = null
): Parcelable