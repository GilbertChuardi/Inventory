package com.example.inventory.ui.fragment.pembelian.beli.barang

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.CustomOnItemClickListener
import com.example.inventory.R
import com.example.inventory.databinding.ActivityPembelianPilihBarangBinding
import com.example.inventory.model.SupplierBarangModel
import com.example.inventory.ui.fragment.MainActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PembelianPilihBarangActivity : AppCompatActivity(), View.OnClickListener {

    private var dataItem = ArrayList<String>()
    private lateinit var binding: ActivityPembelianPilihBarangBinding
    private lateinit var adapter: ProductFirestoreRecyclerAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembelianPilihBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        dataItem = intent.getStringArrayListExtra(EXTRA_DATA)!!

        db = FirebaseFirestore.getInstance()
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("supplier_barang").whereIn("supplier_id", dataItem).orderBy(
            "kode_barang",
            Query.Direction.ASCENDING
        )
        val options =
            FirestoreRecyclerOptions.Builder<SupplierBarangModel>()
                .setQuery(query, SupplierBarangModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter

        binding.btnLanjutPembelianPilihBeliBarang.setOnClickListener(this)
        binding.btnBatalPembelianPilihBeliBarang.setOnClickListener(this)
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<SupplierBarangModel>) :
        FirestoreRecyclerAdapter<SupplierBarangModel, ProductViewHolder>(options) {
        var checkedItem = ArrayList<String>()
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: SupplierBarangModel
        ) {
            holder.setIsRecyclable(false)
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_transaksi)
            val tvKodeBarang: TextView =
                holder.itemView.findViewById(R.id.tv_harga_barang_transaksi)
            val checkBox: CheckBox = holder.itemView.findViewById(R.id.cb_transaksi)
            tvNamaBarang.text = model.nama_barang
            tvKodeBarang.text = model.kode_barang

            if (checkedItem.size != 0) {
                var i = 0
                var flag = false
                while (i < checkedItem.size) {
                    if (checkedItem[i] == model.id) {
                        checkBox.isChecked = true
                        flag = true
                        break
                    }
                    i++
                }
                if (!flag) {
                    checkBox.isChecked = false
                }
            }

            checkBox.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val myCheckBox = view as CheckBox
                        if (myCheckBox.isChecked) {
                            model.isSelected = true
                            checkedItem.add(model.id)
                        } else if (!myCheckBox.isChecked) {
                            model.isSelected = false
                            checkedItem.remove(model.id)
                        }
                    }
                }
            ))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_data_pilih_barang_cb, parent, false)
            return ProductViewHolder(view)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_lanjut_pembelian_pilih_beli_barang -> {
                when {
                    adapter.checkedItem.size in 1..10 -> {
                        val intent = Intent(this, PembelianBeliBarangActivity::class.java)
                        intent.putStringArrayListExtra(
                            PembelianBeliBarangActivity.EXTRA_DATA,
                            adapter.checkedItem
                        )
                        intent.putExtra(PembelianBeliBarangActivity.EXTRA_DATA_NAMA, dataItem[1])
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                    adapter.checkedItem.size > 10 -> {
                        Toast.makeText(
                            this,
                            "Tidak bisa membeli lebih dari 10 item",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            this,
                            "Tidak ada barang yang di ceklist",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }

            R.id.btn_batal_pembelian_pilih_beli_barang -> {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}