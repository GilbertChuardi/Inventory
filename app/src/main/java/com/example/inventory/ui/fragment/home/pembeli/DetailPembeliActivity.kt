package com.example.inventory.ui.fragment.home.pembeli

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityDetailPembeliBinding
import com.example.inventory.model.PembeliModel
import com.google.firebase.firestore.FirebaseFirestore

class DetailPembeliActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailPembeliBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var dataItem: PembeliModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPembeliBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        dataItem = intent.getParcelableExtra(EXTRA_DATA)!!

        binding.etNamaPembeliDetail.setText(dataItem.nama_pembeli)
        binding.etNotelPembeliDetail.setText(dataItem.notel_pembeli)
        binding.etAlamatPembeliDetail.setText(dataItem.alamat_pembeli)

        binding.btnBackPembeliDetail.setOnClickListener(this)
        binding.btnSimpanPembeliDetail.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back_pembeli_detail -> finish()
            R.id.btn_simpan_pembeli_detail -> updateItem()
        }
    }

    private fun updateItem() {
        if (binding.etNamaPembeliDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etNotelPembeliDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etAlamatPembeliDetail.text?.trim()?.isNotEmpty() == true
        ) {
            db.collection("customer")
                .document(dataItem.id)
                .update(
                    mapOf(
                        "nama_pembeli" to binding.etNamaPembeliDetail.text.toString(),
                        "notel_pembeli" to binding.etNotelPembeliDetail.text.toString(),
                        "alamat_pembeli" to binding.etAlamatPembeliDetail.text.toString(),
                        "id" to dataItem.id
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Update", Toast.LENGTH_SHORT).show()
                }

            finish()
        } else if (binding.etNamaPembeliDetail.text?.trim()?.isEmpty() == true) {
            binding.etNamaPembeliDetail.error = "Masukkan nama pembeli"
        } else if (binding.etNotelPembeliDetail.text?.trim()?.isEmpty() == true) {
            binding.etNotelPembeliDetail.error = "Masukkan nomor telepon"
        } else if (binding.etAlamatPembeliDetail.text?.trim()?.isEmpty() == true) {
            binding.etAlamatPembeliDetail.error = "Masukkan alamat pembeli"
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}