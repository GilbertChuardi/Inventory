package com.example.inventory.ui.fragment.laporan.view.daftar

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityDetailDaftarBinding
import com.example.inventory.model.DaftarModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat

class DetailDaftarActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var dataItem: DaftarModel
    private lateinit var binding: ActivityDetailDaftarBinding
    private lateinit var db: FirebaseFirestore
    private val numberFormat1: NumberFormat = NumberFormat.getCurrencyInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDaftarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        dataItem = intent.getParcelableExtra(EXTRA_DATA)!!
        supportActionBar?.hide()

        numberFormat1.maximumFractionDigits = 0
        val convert = numberFormat1.format(dataItem.total_harga)

        val sb = StringBuilder()
        var i = 0
        while (i < dataItem.data_nama_item.size) {
            if (i != dataItem.data_nama_item.size - 1) {
                sb.append(
                    "- ",
                    dataItem.data_nama_item[i],
                    " : ",
                    dataItem.data_total_item[i],
                    " ",
                    dataItem.data_satuan_item[i],
                    "\n"
                )
            } else {
                sb.append(
                    "- ",
                    dataItem.data_nama_item[i],
                    " : ",
                    dataItem.data_total_item[i],
                    " ",
                    dataItem.data_satuan_item[i]
                )
            }
            i++
        }

        binding.tvNamaPembeliDaftar.text = "Nama Pembeli: " + dataItem.nama
        binding.tvDataDaftar.text = sb.toString()
        binding.tvTanggalDaftar.text = dataItem.tanggal.toDate().toLocaleString().toString()
        binding.tvTotalHargaDaftar.text = "Rp. " + convert.removeRange(0, 1)
        binding.etKeteranganDaftar.setText(dataItem.keterangan)

        binding.btnBackDetailDaftar.setOnClickListener(this)
        binding.btnUpdateDetailDaftar.setOnClickListener(this)
        binding.btnHapusDetailDaftar.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back_detail_daftar -> finish()
            R.id.btn_update_detail_daftar -> updateItem()
            R.id.btn_hapus_detail_daftar -> alertDialogDelete()
        }
    }

    private fun updateItem() {
        db.collection("daftar_piutang").document(dataItem.id)
            .update("keterangan", binding.etKeteranganDaftar.text.toString())
            .addOnSuccessListener {
                Toast.makeText(this, "Data sudah terupdate", Toast.LENGTH_SHORT).show()
            }
    }

    private fun alertDialogDelete() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Apakah data ini sudah lunas?")
            .setPositiveButton(
                "Hapus"
            ) { _, _ -> deleteItem() }
            .setNegativeButton("Batal") { _, _ -> }
            .show()
    }

    private fun deleteItem() {
        db.collection("daftar_piutang")
            .document(dataItem.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Data sudah terhapus", Toast.LENGTH_SHORT).show()
            }

        finish()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}