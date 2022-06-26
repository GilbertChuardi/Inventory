package com.example.inventory.ui.fragment.inventaris.tambah

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityTambahBinding
import com.google.firebase.firestore.FirebaseFirestore

class TambahActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTambahBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        binding.btnSubmit.setOnClickListener(this)
        binding.btnBackTambah.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_submit -> createProject()
            R.id.btn_back_tambah -> finish()
        }
    }

    private fun createProject() {
        val nama_barang = binding.etNamaBarang.text.toString().trim()
        val kode_barang = binding.etKodeBarang.text.toString().trim()
        val merek_barang = binding.etMerekBarang.text.toString().trim()
        val nama_supplier = binding.etNamaSupplier.text.toString().trim()
        val harga_barang = binding.etHargaBarang.text.toString().trim()
        val jumlah_barang = binding.etJumlahBarang.text.toString().trim()
        val id = db.collection("Inventaris").document().id

        if (nama_barang.isNotEmpty() &&
            kode_barang.isNotEmpty() &&
            merek_barang.isNotEmpty() &&
            nama_supplier.isNotEmpty() &&
            harga_barang.isNotEmpty() &&
            jumlah_barang.isNotEmpty()
        ) {
            val data = hashMapOf(
                "nama_barang" to nama_barang,
                "kode_barang" to kode_barang,
                "merek_barang" to merek_barang,
                "nama_supplier" to nama_supplier,
                "harga_barang" to harga_barang.toInt(),
                "jumlah_barang" to jumlah_barang.toInt(),
                "id" to id
            )

            db.collection("Inventaris")
                .document(id)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
        } else if (binding.etNamaBarang.text?.trim()?.isEmpty() == true) {
            binding.etNamaBarang.error = "Masukkan nama barang"
        } else if (binding.etKodeBarang.text?.trim()?.isEmpty() == true) {
            binding.etKodeBarang.error = "Masukkan kode barang"
        } else if (binding.etMerekBarang.text?.trim()?.isEmpty() == true) {
            binding.etMerekBarang.error = "Masukkan merek barang"
        } else if (binding.etNamaSupplier.text?.trim()?.isEmpty() == true) {
            binding.etNamaSupplier.error = "Masukkan nama supplier"
        } else if (binding.etHargaBarang.text?.trim()?.isEmpty() == true) {
            binding.etHargaBarang.error = "Masukkan harga barang"
        } else if (binding.etJumlahBarang.text?.trim()?.isEmpty() == true) {
            binding.etJumlahBarang.error = "Masukkan jumlah barang"
        }

    }
}