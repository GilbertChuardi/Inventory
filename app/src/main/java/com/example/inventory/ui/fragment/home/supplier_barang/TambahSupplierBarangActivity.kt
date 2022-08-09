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
            R.id.btn_simpan_supplier_barang_tambah -> createData()
        }
    }

    private fun createData() {
        readData(object : MyCallback {
            override fun onCallback(value: String) {
                if (value == "adakode") {
                    Toast.makeText(
                        this@TambahSupplierBarangActivity,
                        "Kode/Nama Supplier sudah ada",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (value == "kodekosong") {
                    tambahSupplier()
                }
            }
        })
    }

    private fun tambahSupplier() {
        val namaSupplierBarang = binding.etNamaSupplierBarangTambah.text.toString().trim()
        val kodeSupplierBarang = binding.etKodeSupplierBarangTambah.text.toString().trim()
        val supplierId = data
        val id = db.collection("supplier_barang").document().id

        if (namaSupplierBarang.isNotEmpty() &&
            kodeSupplierBarang.isNotEmpty()
        ) {
            val data = hashMapOf(
                "nama_barang" to namaSupplierBarang,
                "kode_barang" to kodeSupplierBarang,
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
        } else if (binding.etKodeSupplierBarangTambah.text?.trim()?.isEmpty() == true) {
            binding.etKodeSupplierBarangTambah.error = "Masukkan kode barang"
        }
    }

    private fun readData(myCallback: MyCallback) {
        val cekKode = db.collection("supplier_barang")
            .whereEqualTo("kode_barang", binding.etKodeSupplierBarangTambah.text.toString().trim())
        var kode = "kodekosong"
        cekKode.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        kode = "adakode"
                    }
                }
            }
        val cekNama = db.collection("supplier_barang")
            .whereEqualTo("nama_barang", binding.etNamaSupplierBarangTambah.text.toString().trim())
        cekNama.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        kode = "adakode"
                    }
                    myCallback.onCallback(kode)
                }
            }
    }

    interface MyCallback {
        fun onCallback(value: String)
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}