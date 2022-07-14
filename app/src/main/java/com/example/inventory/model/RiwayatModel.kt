package com.example.inventory.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

var tsrm = Timestamp(0, 0)

@Parcelize
class RiwayatModel(
    val data_nama_item: List<String> = arrayOf("0").toList(),
    val data_total_item: List<Int> = arrayOf(0).toList(),
    val nama: String = "",
    val tanggal: Timestamp = tsrm,
    val total_harga: Int = 0
) : Parcelable