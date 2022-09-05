package com.example.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class BarangModel(
    val harga_beli: Int = 0,
    val harga_jual: Int = 0,
    val id: String = "",
    val jumlah_barang: Int = 0,
    val nama_barang: String = "",
    val nama_supplier: String = "",
    val satuan_barang: String = "",
    val keywords:  List<String> = arrayOf("0").toList(),
    val supplier_id: String = ""
) : Parcelable {
    var isSelected: Boolean = false
}