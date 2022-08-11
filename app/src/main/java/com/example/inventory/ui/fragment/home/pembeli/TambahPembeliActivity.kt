package com.example.inventory.ui.fragment.home.pembeli

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityTambahPembeliBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class TambahPembeliActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTambahPembeliBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahPembeliBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()

        binding.btnBackPembeliTambah.setOnClickListener(this)
        binding.btnBatalPembeliTambah.setOnClickListener(this)
        binding.btnSimpanPembeliTambah.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back_pembeli_tambah -> finish()
            R.id.btn_batal_pembeli_tambah -> finish()
            R.id.btn_simpan_pembeli_tambah -> createData()
        }
    }

    private fun createData() {
        readData(object : MyCallback {
            override fun onCallback(value: String) {
                if (value == "ada") {
                    Toast.makeText(
                        this@TambahPembeliActivity,
                        "Nama pembeli sudah ada",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (value == "kosong") {
                    tambahPembeli()
                }
            }
        })
    }

    private fun tambahPembeli() {
        val namaPembeli = binding.etNamaPembeliTambah.text.toString().trim()
        val notelPembeli = binding.etNotelPembeliTambah.text.toString().trim()
        val alamatPembeli = binding.etAlamatPembeliTambah.text.toString().trim()
        val id = db.collection("customer").document().id
        val t = Timestamp.now()
        val ts = Timestamp(t.seconds, 0)

        if (namaPembeli.isNotEmpty() &&
            notelPembeli.isNotEmpty() &&
            alamatPembeli.isNotEmpty()
        ) {
            val data = hashMapOf(
                "tanggal_buat" to ts,
                "nama_pembeli" to namaPembeli,
                "notel_pembeli" to notelPembeli,
                "alamat_pembeli" to alamatPembeli,
                "id" to id
            )

            db.collection("customer")
                .document(id)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show()
                    finish()
                }

        } else if (binding.etNamaPembeliTambah.text?.trim()?.isEmpty() == true) {
            binding.etNamaPembeliTambah.error = "Masukkan nama pembeli"
        } else if (binding.etNotelPembeliTambah.text?.trim()?.isEmpty() == true) {
            binding.etNotelPembeliTambah.error = "Masukkan nomor telepon"
        } else if (binding.etAlamatPembeliTambah.text?.trim()?.isEmpty() == true) {
            binding.etAlamatPembeliTambah.error = "Masukkan alamat pembeli"
        }
    }

    private fun readData(myCallback: MyCallback) {
        val cekKode = db.collection("customer")
            .whereEqualTo("nama_pembeli", binding.etNamaPembeliTambah.text.toString().trim())
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