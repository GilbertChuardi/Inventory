package com.example.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SupplierModel(
    val alamat_supplier: String = "",
    val kode_supplier: String = "",
    val nama_supplier: String = "",
    val notel_supplier: String = "",
    val id: String = ""
) : Parcelable