package com.example.inventory.ui.fragment.home.supplier

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityTambahSupplierBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class TambahSupplierActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTambahSupplierBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahSupplierBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()

        binding.btnBackSupplierTambah.setOnClickListener(this)
        binding.btnBatalSupplierTambah.setOnClickListener(this)
        binding.btnSimpanSupplierTambah.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back_supplier_tambah -> finish()
            R.id.btn_batal_supplier_tambah -> finish()
            R.id.btn_simpan_supplier_tambah -> createData()
        }
    }

    private fun createData() {
        readData(object : MyCallback {
            override fun onCallback(value: String) {
                if (value == "ada") {
                    Toast.makeText(
                        this@TambahSupplierActivity,
                        "Nama Supplier sudah ada",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (value == "kosong") {
                    tambahSupplier()
                }
            }
        })
    }

    private fun tambahSupplier() {
        val namaSupplier = binding.etNamaSupplierTambah.text.toString().trim()
        val notelSupplier = binding.etNotelSupplierTambah.text.toString().trim()
        val alamatSupplier = binding.etAlamatSupplierTambah.text.toString().trim()
        val id = db.collection("supplier").document().id
        val t = Timestamp.now()
        val ts = Timestamp(t.seconds, 0)

        if (namaSupplier.isNotEmpty() &&
            notelSupplier.isNotEmpty() &&
            alamatSupplier.isNotEmpty()
        ) {
            val data = hashMapOf(
                "tanggal_buat" to ts,
                "nama_supplier" to namaSupplier,
                "notel_supplier" to notelSupplier,
                "alamat_supplier" to alamatSupplier,
                "id" to id
            )

            db.collection("supplier")
                .document(id)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show()
                    finish()
                }

        } else if (binding.etNamaSupplierTambah.text?.trim()?.isEmpty() == true) {
            binding.etNamaSupplierTambah.error = "Masukkan nama supplier"
        } else if (binding.etNotelSupplierTambah.text?.trim()?.isEmpty() == true) {
            binding.etNotelSupplierTambah.error = "Masukkan nomor telepon"
        } else if (binding.etAlamatSupplierTambah.text?.trim()?.isEmpty() == true) {
            binding.etAlamatSupplierTambah.error = "Masukkan alamat supplier"
        }
    }

    private fun readData(myCallback: MyCallback) {
        val cekKode = db.collection("supplier")
            .whereEqualTo("nama_supplier", binding.etNamaSupplierTambah.text.toString().trim())
        cekKode.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var kode = "kosong"
                    for (document in task.result) {
                        kode = "ada"
                    }
                    myCallback.onCallback(kode)
                }
            }
    }

    interface MyCallback {
        fun onCallback(value: String)
    }
}