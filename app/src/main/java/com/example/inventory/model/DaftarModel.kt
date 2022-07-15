package com.example.inventory.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

var tsdm = Timestamp(0, 0)

@Parcelize
class DaftarModel(
    val data_nama_item: List<String> = arrayOf("0").toList(),
    val data_satuan_item: List<String> = arrayOf("0").toList(),
    val data_total_item: List<Int> = arrayOf(0).toList(),
    val nama: String = "",
    val tanggal: Timestamp = tsdm,
    val total_harga: Int = 0,
    val keterangan: String = "",
    val id: String = ""
) : Parcelable