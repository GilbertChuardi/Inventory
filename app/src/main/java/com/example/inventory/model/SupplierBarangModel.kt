package com.example.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SupplierBarangModel(
    val nama_barang: String = "",
    val id: String = ""
) : Parcelable {
    var isSelected: Boolean = false
}