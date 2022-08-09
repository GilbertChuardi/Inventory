package com.example.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class BarangModel(
    val harga_beli: Int = 0,
    val harga_jual: Int = 0,
    val id: String = "",
    val jumlah_barang: Int = 0,
    val kode_barang: String = "",
    val nama_barang: String = "",
    val nama_supplier: String = "",
    val satuan_barang: String = "",
    val supplier_id: String = ""
) : Parcelable {
    var isSelected: Boolean = false
}