package com.example.inventory.ui.fragment.home.supplier

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.CustomOnItemClickListener
import com.example.inventory.R
import com.example.inventory.databinding.FragmentTabSupplierBinding
import com.example.inventory.model.SupplierModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class TabFragmentSupplier : Fragment(), View.OnClickListener {

    private var _binding: FragmentTabSupplierBinding? = null
    private val binding get() = _binding!!
    private var adapter: ProductFirestoreRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabSupplierBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("supplier")
        val options = FirestoreRecyclerOptions.Builder<SupplierModel>()
            .setQuery(query, SupplierModel::class.java)
            .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter

        binding.btnTambahSupplier.setOnClickListener(this)
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<SupplierModel>) :
        FirestoreRecyclerAdapter<SupplierModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: SupplierModel
        ) {
            val tvNamaPembeli: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_riwayat)
            val tvKode: TextView = holder.itemView.findViewById(R.id.tv_kode_barang_riwayat)
            val tvJumlah: TextView = holder.itemView.findViewById(R.id.tv_jumlah_barang_riwayat)
            val cvItem: CardView = holder.itemView.findViewById(R.id.cv_item_riwayat)
            tvNamaPembeli.text = model.nama_supplier
            tvKode.text = model.kode_supplier
            tvJumlah.text = ""
            cvItem.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val intent = Intent(activity, DetailSupplierActivity::class.java)
                        intent.putExtra(DetailSupplierActivity.EXTRA_DATA, model)
                        activity?.startActivity(intent)
                    }
                }
            ))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_data_riwayat, parent, false)
            return ProductViewHolder(view)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_tambah_supplier -> startActivity(
                Intent(
                    context,
                    TambahSupplierActivity::class.java
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) {
            adapter!!.stopListening()
        }
    }
}