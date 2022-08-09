package com.example.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DetailInvoiceModel(
    val barang_id: String = "",
    val harga_beli: Int = 0,
    val id: String = "",
    val invoice_id: String = "",
    val jumlah_barang: Int = 0,
    val nama_barang: String = "",
    val nama_supplier: String = "",
    val supplier_id: String = "",
) : Parcelable {
    var isSelected: Boolean = false
}