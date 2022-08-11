package com.example.inventory.ui.fragment.home.supplier_barang

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityTambahSupplierBarangBinding
import com.google.firebase.firestore.FirebaseFirestore

class TambahSupplierBarangActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTambahSupplierBarangBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var data: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahSupplierBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        data = intent.getStringExtra(EXTRA_DATA)!!

        db = FirebaseFirestore.getInstance()

        binding.btnBackSupplierBarangTambah.setOnClickListener(this)
        binding.btnBatalSupplierBarangTambah.setOnClickListener(this)
        binding.btnSimpanSupplierBarangTambah.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back_supplier_barang_tambah -> finish()
            R.id.btn_batal_supplier_barang_tambah -> finish()
            R.id.btn_simpan_supplier_barang_tambah -> tambahSupplier()
        }
    }

    private fun tambahSupplier() {
        val namaSupplierBarang = binding.etNamaSupplierBarangTambah.text.toString().trim()
        val supplierId = data
        val id = db.collection("supplier_barang").document().id

        if (namaSupplierBarang.isNotEmpty()) {
            val data = hashMapOf(
                "nama_barang" to namaSupplierBarang,
                "supplier_id" to supplierId,
                "id" to id
            )

            db.collection("supplier_barang")
                .document(id)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show()
                    finish()
                }

        } else if (binding.etNamaSupplierBarangTambah.text?.trim()?.isEmpty() == true) {
            binding.etNamaSupplierBarangTambah.error = "Masukkan nama barang"
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}