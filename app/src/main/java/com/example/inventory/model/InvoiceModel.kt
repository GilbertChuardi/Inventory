package com.example.inventory.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

var tsim = Timestamp(0, 0)

@Parcelize
class InvoiceModel(
    val supplier_id: String = "",
    val tanggal_invoice: Timestamp = tsim,
    val total_pembelian: Int = 0,
    val id: String = ""
) : Parcelable