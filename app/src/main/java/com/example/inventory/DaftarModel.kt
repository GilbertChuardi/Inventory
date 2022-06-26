package com.example.inventory

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DaftarModel(
    val kode_transaksi : String = "",
    val nama_pembeli : String = "",
    val total_harga_transaksi: Int = 0
) : Parcelable