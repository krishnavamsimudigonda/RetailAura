package com.adina.retailaura.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Store(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val location: String = ""
) : Parcelable