package com.example.inventory.ui.fragment.home.supplier_barang

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityDetailSupplierBarangBinding
import com.example.inventory.model.SupplierBarangModel
import com.google.firebase.firestore.FirebaseFirestore

class DetailSupplierBarangActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailSupplierBarangBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var dataItem: SupplierBarangModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSupplierBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        dataItem = intent.getParcelableExtra(EXTRA_DATA)!!

        binding.etNamaSupplierBarangDetail.setText(dataItem.nama_barang)
        binding.etKodeSupplierBarangDetail.setText(dataItem.kode_barang)

        binding.btnBackSupplierBarangDetail.setOnClickListener(this)
        binding.btnSimpanSupplierBarangDetail.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back_supplier_barang_detail -> finish()
            R.id.btn_simpan_supplier_barang_detail -> updateItem()
        }
    }

    private fun updateItem() {
        if (binding.etNamaSupplierBarangDetail.text?.trim()?.isNotEmpty() == true &&
            binding.etKodeSupplierBarangDetail.text?.trim()?.isNotEmpty() == true
        ) {
            db.collection("supplier_barang")
                .document(dataItem.id)
                .update(
                    mapOf(
                        "nama_barang" to binding.etNamaSupplierBarangDetail.text.toString(),
                        "kode_barang" to binding.etKodeSupplierBarangDetail.text.toString(),
                        "supplier_id" to dataItem.supplier_id,
                        "id" to dataItem.id
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Update", Toast.LENGTH_SHORT).show()
                }

            finish()
        } else if (binding.etNamaSupplierBarangDetail.text?.trim()?.isEmpty() == true) {
            binding.etNamaSupplierBarangDetail.error = "Masukkan nama barang"
        } else if (binding.etKodeSupplierBarangDetail.text?.trim()?.isEmpty() == true) {
            binding.etKodeSupplierBarangDetail.error = "Masukkan kode barang"
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}