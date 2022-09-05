package com.example.inventory.ui.fragment.laporan.view.daftar

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityDetailDaftarBinding
import com.example.inventory.model.DaftarModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
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
        dataItem = intent.getParcelableExtra(EXTRA_DATA)!!
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()

        numberFormat1.maximumFractionDigits = 0
        val convert = numberFormat1.format(dataItem.total_harga)
        val convert1 = numberFormat1.format(dataItem.total_bayar)
        val convert2 = numberFormat1.format(dataItem.total_harga-dataItem.total_bayar)

        db.collection("transaksip_penjualan").whereEqualTo("id", dataItem.transaksi_penjualan_id)
            .get()
            .addOnSuccessListener { documents ->
                val sb = StringBuilder()
                for (document in documents) {
                    sb.append(
                        "- ",
                        document["nama_barang"],
                        " : ",
                        document["jumlah_jual"],
                        " ",
                        document["satuan_barang"],
                        "\n"
                    )
                }
                binding.tvNamaPembeliDaftar.text = "Nama Pembeli: " + dataItem.nama_pembeli
                binding.tvDataDaftar.text = sb.toString()
                binding.tvTanggalDaftar.text =
                    dataItem.tanggal_penjualan.toDate().toLocaleString().toString()
                binding.tvTotalHargaDaftar.text = "Rp. " + convert.removeRange(0, 1) + "\nTotal yang sudah dibayar: Rp. " +convert1.removeRange(0,1) + "\nTotal belum dibayar: Rp. " + convert2.removeRange(0,1)
                binding.etKeteranganDaftar.setText(dataItem.keterangan)
            }

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
        if(binding.etTotalBayarDaftar.text.toString().isNotEmpty()) {
            db.collection("transaksip").document(dataItem.id)
                .update(
                    mapOf(
                        "keterangan" to binding.etKeteranganDaftar.text.toString(),
                        "total_bayar" to FieldValue.increment(binding.etTotalBayarDaftar.text.toString().toLong())
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Data sudah terupdate", Toast.LENGTH_SHORT).show()
                }
            finish()
        }
    }

    private fun alertDialogDelete() {
        AlertDialog.Builder(this)
            .setTitle("Hapus dari piutang")
            .setMessage("Apakah data ini sudah lunas?")
            .setPositiveButton(
                "Hapus"
            ) { _, _ -> deleteItem() }
            .setNegativeButton("Batal") { _, _ -> }
            .show()
    }

    private fun deleteItem() {

        val t = Timestamp.now()
        val ts = Timestamp(t.seconds, 0)

        val dataInvoice = hashMapOf(
            "nama_pembeli" to dataItem.nama_pembeli,
            "tanggal_penjualan" to ts,
            "total_harga" to dataItem.total_harga,
            "total_profit" to dataItem.total_profit,
            "transaksi_penjualan_id" to dataItem.transaksi_penjualan_id
        )

        db.collection("transaksi")
            .document()
            .set(dataInvoice)
            .addOnSuccessListener {
                Log.d("Tag", "invoice transaksi sudah ditambah")
            }

        db.collection("transaksip_penjualan").whereEqualTo("id", dataItem.transaksi_penjualan_id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val data = hashMapOf(
                        "id" to document["id"],
                        "jumlah_jual" to document["jumlah_jual"].toString().toInt(),
                        "nama_barang" to document["nama_barang"],
                        "satuan_barang" to document["satuan_barang"]
                    )

                    db.collection("transaksi_penjualan")
                        .document()
                        .set(data)
                        .addOnSuccessListener {
                            Log.d("Tag", "transaksip sudah dipindah")
                        }

                    db.collection("transaksip_penjualan")
                        .document(document["penjualan_id"].toString())
                        .delete()
                        .addOnSuccessListener {
                            Log.d("Tag", "transaksip_penjualan sudah didelete")
                        }
                }
            }


        db.collection("transaksip")
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