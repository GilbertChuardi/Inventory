package com.example.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TransaksiModel(
    val harga_barang: Int = 0,
    val id: String = "",
    val jumlah_barang: Int = 0,
    var nama_barang: String = "",
    var satuan_barang: String = ""
) : Parcelable {
    var isSelected: Boolean = false
}