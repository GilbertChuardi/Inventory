package com.example.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DataModel(
    val harga_barang: Int = 0,
    val id: String = "",
    val jumlah_barang: Int = 0,
    val kode_barang: String = "",
    val merek_barang: String = "",
    var nama_barang: String = "",
    var nama_supplier: String = ""
) : Parcelable