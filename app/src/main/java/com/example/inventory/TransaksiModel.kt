package com.example.inventory

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TransaksiModel(
    val harga_barang: Int = 0,
    val id: String = "",
    val jumlah_barang: Int = 0,
    val kode_barang: String = "",
    val merek_barang: String = "",
    var nama_barang: String = "",
    var nama_supplier: String = ""
) : Parcelable {
    var isSelected: Boolean = false
}