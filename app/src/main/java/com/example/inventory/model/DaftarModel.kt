package com.example.inventory.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

var tsdm = Timestamp(0, 0)

@Parcelize
class DaftarModel(
    val id: String = "",
    val keterangan: String = "",
    val nama_pembeli: String = "",
    val tanggal_penjualan: Timestamp = tsdm,
    val total_harga: Int = 0,
    val total_profit: Int = 0,
    val transaksi_penjualan_id: String = ""
) : Parcelable