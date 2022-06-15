package com.example.inventory.ui.tambah

import android.os.Bundle
import android.util.Log
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

        db = FirebaseFirestore.getInstance()
        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_submit -> createProject()
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
                    Log.d("Tambah sukses", "DocumentSnapshot successfully written!")
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding document", Toast.LENGTH_SHORT).show()
                    Log.w("tambah gagal", "Error adding document", e)
                }
        }

    }
}