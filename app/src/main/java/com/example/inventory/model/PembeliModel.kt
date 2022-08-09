package com.example.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PembeliModel(
    val alamat_pembeli: String = "",
    val kode_pembeli: String = "",
    val nama_pembeli: String = "",
    val notel_pembeli: String = "",
    val id: String = ""
) : Parcelable