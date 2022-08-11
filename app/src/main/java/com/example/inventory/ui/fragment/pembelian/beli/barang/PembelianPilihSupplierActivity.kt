package com.example.inventory.ui.fragment.pembelian.beli.barang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.CustomOnItemClickListener
import com.example.inventory.R
import com.example.inventory.databinding.ActivityPembelianPilihSupplierBinding
import com.example.inventory.model.SupplierModel
import com.example.inventory.ui.fragment.MainActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class PembelianPilihSupplierActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPembelianPilihSupplierBinding
    private lateinit var adapter: ProductFirestoreRecyclerAdapter
    private lateinit var db: FirebaseFirestore
    private var selectedItem: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembelianPilihSupplierBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        val query = db.collection("supplier")
        val options =
            FirestoreRecyclerOptions.Builder<SupplierModel>()
                .setQuery(query, SupplierModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter
        binding.btnLanjutPembelianPilihSupplier.setOnClickListener(this)
        binding.btnBatalPembelianPilihSupplier.setOnClickListener(this)
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<SupplierModel>) :
        FirestoreRecyclerAdapter<SupplierModel, ProductViewHolder>(options) {
        var checkedItem = ArrayList<String>()
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: SupplierModel
        ) {
            holder.setIsRecyclable(false)
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_transaksi)
            val tvHargaBarang: TextView =
                holder.itemView.findViewById(R.id.tv_harga_barang_transaksi)
            val rbSupplier: RadioButton = holder.itemView.findViewById(R.id.rb_supplier)
            tvNamaBarang.text = model.nama_supplier
            tvHargaBarang.text = model.alamat_supplier
            if (selectedItem == position) {
                rbSupplier.isChecked = true
                Log.d("TAG", position.toString() + selectedItem.toString() + "jd true")
            } else if (selectedItem != position) {
                rbSupplier.isChecked = false
                Log.d("TAG", position.toString() + selectedItem.toString() + "jd false")
            }
            rbSupplier.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        selectedItem = position
                        if (rbSupplier.isChecked) {
                            rbSupplier.isChecked = true
                            selectedItem = position
                            notifyDataSetChanged()
                            Log.d(
                                "TAG",
                                position.toString() + selectedItem.toString() + "jadi select"
                            )
                        }
                        checkedItem.removeAll(checkedItem)
                        checkedItem.add(model.id)
                        checkedItem.add(model.nama_supplier)

                    }
                }
            ))

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_data_pilih_supplier_rb, parent, false)
            return ProductViewHolder(view)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_lanjut_pembelian_pilih_supplier -> next()
            R.id.btn_batal_pembelian_pilih_supplier -> {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }
    }

    private fun next() {
        if (selectedItem != -1) {
            val intent = Intent(this, PembelianPilihBarangActivity::class.java)
            intent.putStringArrayListExtra(
                PembelianPilihBarangActivity.EXTRA_DATA,
                adapter.checkedItem
            )
            startActivity(intent)
            overridePendingTransition(0, 0)
        } else if (selectedItem == -1) {
            Toast.makeText(this, "Belum ada yang terpilih", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}