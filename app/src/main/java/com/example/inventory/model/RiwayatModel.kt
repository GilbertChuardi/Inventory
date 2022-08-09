package com.example.inventory.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

var tsrm = Timestamp(0, 0)

@Parcelize
class RiwayatModel(
    val nama_pembeli: String = "",
    val tanggal_penjualan: Timestamp = tsrm,
    val total_harga: Int = 0,
    val total_profit: Int = 0,
    val transaksi_penjualan_id: String = ""
) : Parcelable