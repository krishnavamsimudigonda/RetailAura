package com.adina.retailaura.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val discountPrice: Double = 0.0,
    var quantity: Int = 0,
    val imageUrl: String = ""
) : Parcelable

