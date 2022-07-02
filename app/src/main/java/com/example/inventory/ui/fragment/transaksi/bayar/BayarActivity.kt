package com.example.inventory.ui.fragment.transaksi.bayar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.CustomOnItemClickListener
import com.example.inventory.DataModel
import com.example.inventory.R
import com.example.inventory.databinding.ActivityBayarBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class BayarActivity : AppCompatActivity() {

    private var dataItem = ArrayList<String>()
    private lateinit var binding: ActivityBayarBinding
    private lateinit var adapter: ProductFirestoreRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBayarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        dataItem = intent.getStringArrayListExtra(EXTRA_DATA_BAYAR)!!

        binding.recyclerViewBayar.layoutManager = LinearLayoutManager(applicationContext)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("Inventaris").whereIn("id", dataItem)
        val options =
            FirestoreRecyclerOptions.Builder<DataModel>().setQuery(query, DataModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerViewBayar.adapter = adapter
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<DataModel>) :
        FirestoreRecyclerAdapter<DataModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: DataModel) {
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_bayar)
            val tvHargaBarang: TextView = holder.itemView.findViewById(R.id.tv_harga_barang_bayar)
            val tvJumlahBarang: TextView = holder.itemView.findViewById(R.id.tv_jumlah_barang_bayar)
            val btnDeleteBarang: ImageButton = holder.itemView.findViewById(R.id.btn_delete_bayar)
            tvNamaBarang.text = model.nama_barang
            tvHargaBarang.text = model.kode_barang
            tvJumlahBarang.text = model.jumlah_barang.toString()
            btnDeleteBarang.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback{
                    override fun onItemClicked(view: View, position: Int) {
                        dataItem.remove(model.id)
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                }
            ))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_data_bayar, parent, false)
            return ProductViewHolder(view)
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

    companion object {
        const val EXTRA_DATA_BAYAR = "extra_data_bayar"
    }
}