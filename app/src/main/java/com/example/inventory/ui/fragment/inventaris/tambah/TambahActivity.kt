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
        val namaBarang = binding.etNamaBarang.text.toString().trim()
        val kodeBarang = binding.etKodeBarang.text.toString().trim()
        val merekBarang = binding.etMerekBarang.text.toString().trim()
        val namaSupplier = binding.etNamaSupplier.text.toString().trim()
        val hargaBarang = binding.etHargaBarang.text.toString().trim()
        val jumlahBarang = binding.etJumlahBarang.text.toString().trim()
        val satuanBarang = binding.etSatuanBarang.text.toString().trim()
        val id = db.collection("Inventaris").document().id

        if (namaBarang.isNotEmpty() &&
            kodeBarang.isNotEmpty() &&
            merekBarang.isNotEmpty() &&
            namaSupplier.isNotEmpty() &&
            hargaBarang.isNotEmpty() &&
            jumlahBarang.isNotEmpty() &&
            satuanBarang.isNotEmpty()
        ) {
            val data = hashMapOf(
                "nama_barang" to namaBarang,
                "kode_barang" to kodeBarang,
                "merek_barang" to merekBarang,
                "nama_supplier" to namaSupplier,
                "harga_barang" to hargaBarang.toInt(),
                "jumlah_barang" to jumlahBarang.toInt(),
                "satuan_barang" to satuanBarang,
                "id" to id
            )

            db.collection("Inventaris")
                .document(id)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show()
                    finish()
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
        } else if (binding.etSatuanBarang.text?.trim()?.isEmpty() == true) {

        }

    }
}