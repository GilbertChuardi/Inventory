package com.example.inventory.ui.fragment.home.supplier

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityDetailSupplierBinding
import com.example.inventory.model.SupplierModel
import com.google.firebase.firestore.FirebaseFirestore

class DetailSupplierActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailSupplierBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var dataItem: SupplierModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSupplierBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        dataItem = intent.getParcelableExtra(EXTRA_DATA)!!

        db = FirebaseFirestore.getInstance()

        binding.etNamaSupplierDetail.setText(dataItem.nama_supplier)
        binding.etKodeSupplierDetail.setText(dataItem.kode_supplier)
        binding.etNotelSupplierDetail.setText(dataItem.notel_supplier)
        binding.etAlamatSupplierDetail.setText(dataItem.alamat_supplier)

        binding.btnBackSupplierDetail.setOnClickListener(this)
        binding.btnSimpanSupplierDetail.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back_supplier_detail -> finish()
            R.id.btn_simpan_supplier_detail -> updateItem()
        }
    }

    private fun updateItem() {
        if (binding.etNamaSupplierDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etKodeSupplierDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etNotelSupplierDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etAlamatSupplierDetail.text?.trim()?.isNotEmpty() == true
        ) {
            db.collection("supplier")
                .document(dataItem.id)
                .update(
                    mapOf(
                        "nama_supplier" to binding.etNamaSupplierDetail.text.toString(),
                        "kode_supplier" to binding.etKodeSupplierDetail.text.toString(),
                        "notel_supplier" to binding.etNotelSupplierDetail.text.toString(),
                        "alamat_supplier" to binding.etAlamatSupplierDetail.text.toString(),
                        "id" to dataItem.id
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Update", Toast.LENGTH_SHORT).show()
                }

            finish()
        } else if (binding.etNamaSupplierDetail.text?.trim()?.isEmpty() == true) {
            binding.etNamaSupplierDetail.error = "Masukkan nama supplier"
        } else if (binding.etKodeSupplierDetail.text?.trim()?.isEmpty() == true) {
            binding.etKodeSupplierDetail.error = "Masukkan kode supplier"
        } else if (binding.etNotelSupplierDetail.text?.trim()?.isEmpty() == true) {
            binding.etNotelSupplierDetail.error = "Masukkan nomor telepon"
        } else if (binding.etAlamatSupplierDetail.text?.trim()?.isEmpty() == true) {
            binding.etAlamatSupplierDetail.error = "Masukkan alamat supplier"
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}