package com.example.inventory.ui.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.DataModel
import com.example.inventory.R
import com.example.inventory.databinding.ActivityDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var dataItem: DataModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        dataItem = intent.getParcelableExtra(EXTRA_DATA)!!

        binding.etHargaBarangDetail.setText(dataItem.harga_barang.toString())
        binding.etJumlahBarangDetail.setText(dataItem.jumlah_barang.toString())
        binding.etKodeBarangDetail.setText(dataItem.kode_barang)
        binding.etMerekBarangDetail.setText(dataItem.merek_barang)
        binding.etNamaBarangDetail.setText(dataItem.nama_barang)
        binding.etNamaSupplierDetail.setText(dataItem.nama_supplier)

        binding.btnSubmitDetail.setOnClickListener(this)
        binding.btnBatalDetail.setOnClickListener(this)
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_submit_detail -> updateItem()
            R.id.btn_batal_detail -> finish()
        }
    }

    private fun updateItem() {
        db.collection("Inventaris")
            .document(dataItem.id)
            .update(mapOf(
                "nama_barang" to binding.etNamaBarangDetail.text.toString(),
                "kode_barang" to binding.etKodeBarangDetail.text.toString(),
                "merek_barang" to binding.etMerekBarangDetail.text.toString(),
                "nama_supplier" to binding.etNamaSupplierDetail.text.toString(),
                "harga_barang" to binding.etHargaBarangDetail.text.toString().toInt(),
                "jumlah_barang" to binding.etJumlahBarangDetail.text.toString().toInt(),
                "id" to dataItem.id
            ))

        finish()
    }
}