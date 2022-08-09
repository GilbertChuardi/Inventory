package com.example.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SupplierBarangModel(
    val kode_barang: String = "",
    val nama_barang: String = "",
    val supplier_id: String = "",
    val id: String = ""
) : Parcelable {
    var isSelected: Boolean = false
}