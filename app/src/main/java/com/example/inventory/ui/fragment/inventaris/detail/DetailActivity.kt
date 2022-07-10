package com.example.inventory.ui.fragment.inventaris.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
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

        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        dataItem = intent.getParcelableExtra(EXTRA_DATA)!!

        binding.etHargaBarangDetail.setText(dataItem.harga_barang.toString())
        binding.etJumlahBarangDetail.setText(dataItem.jumlah_barang.toString())
        binding.etKodeBarangDetail.setText(dataItem.kode_barang)
        binding.etMerekBarangDetail.setText(dataItem.merek_barang)
        binding.etNamaBarangDetail.setText(dataItem.nama_barang)
        binding.etNamaSupplierDetail.setText(dataItem.nama_supplier)

        binding.btnSubmitDetail.setOnClickListener(this)
        binding.btnBackDetail.setOnClickListener(this)
        binding.btnHapusDetail.setOnClickListener(this)
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_submit_detail -> updateItem()
            R.id.btn_back_detail -> finish()
            R.id.btn_hapus_detail -> onAlertDialog()
        }
    }

    private fun updateItem() {
        if (binding.etNamaBarangDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etKodeBarangDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etMerekBarangDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etNamaSupplierDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etHargaBarangDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etJumlahBarangDetail.text?.trim()?.isNotEmpty() == true
        ) {
            db.collection("Inventaris")
                .document(dataItem.id)
                .update(
                    mapOf(
                        "nama_barang" to binding.etNamaBarangDetail.text.toString(),
                        "kode_barang" to binding.etKodeBarangDetail.text.toString(),
                        "merek_barang" to binding.etMerekBarangDetail.text.toString(),
                        "nama_supplier" to binding.etNamaSupplierDetail.text.toString(),
                        "harga_barang" to binding.etHargaBarangDetail.text.toString().toInt(),
                        "jumlah_barang" to binding.etJumlahBarangDetail.text.toString().toInt(),
                        "id" to dataItem.id
                    )
                )

            finish()
        } else if (binding.etNamaBarangDetail.text?.trim()?.isEmpty() == true) {
            binding.etNamaBarangDetail.error = "Masukkan nama barang"
        } else if (binding.etKodeBarangDetail.text?.trim()?.isEmpty() == true) {
            binding.etKodeBarangDetail.error = "Masukkan kode barang"
        } else if (binding.etMerekBarangDetail.text?.trim()?.isEmpty() == true) {
            binding.etMerekBarangDetail.error = "Masukkan merek barang"
        } else if (binding.etNamaSupplierDetail.text?.trim()?.isEmpty() == true) {
            binding.etNamaSupplierDetail.error = "Masukkan nama supplier"
        } else if (binding.etHargaBarangDetail.text?.trim()?.isEmpty() == true) {
            binding.etHargaBarangDetail.error = "Masukkan harga barang"
        } else if (binding.etJumlahBarangDetail.text?.trim()?.isEmpty() == true) {
            binding.etJumlahBarangDetail.error = "Masukkan jumlah barang"
        }
    }

    private fun deleteItem() {
        db.collection("Inventaris")
            .document(dataItem.id)
            .delete()

        finish()
    }

    private fun onAlertDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Hapus Data")
        builder.setMessage("Data yang terhapus tidak dapat dikembalikan lagi!")

        builder.setPositiveButton(
            "Hapus") { _, _ ->
            deleteItem()
        }
        builder.setNegativeButton(
            "Batal") { _, _ ->
        }
        builder.show()
    }
}