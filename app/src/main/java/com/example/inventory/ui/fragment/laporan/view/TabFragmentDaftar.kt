package com.example.inventory.ui.fragment.laporan.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.DaftarModel
import com.example.inventory.R
import com.example.inventory.databinding.FragmentTabDaftarBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class TabFragmentDaftar : Fragment() {

    private var _binding: FragmentTabDaftarBinding? = null
    private val binding get() = _binding!!
    private var adapter: ProductFirestoreRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTabDaftarBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewDaftar.layoutManager = LinearLayoutManager(context)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("daftar_piutang")
        val options =
            FirestoreRecyclerOptions.Builder<DaftarModel>().setQuery(query, DaftarModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerViewDaftar.adapter = adapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<DaftarModel>) :
        FirestoreRecyclerAdapter<DaftarModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: DaftarModel
        ) {
            val tvNama: TextView = holder.itemView.findViewById(R.id.tv_nama_barang)
            val tvTanggal: TextView = holder.itemView.findViewById(R.id.tv_kode_barang)
            val tvTotal: TextView = holder.itemView.findViewById(R.id.tv_jumlah_barang)
            tvNama.text = model.nama_pembeli
            tvTanggal.text = model.kode_transaksi.subSequence(0, 10)
            tvTotal.text = model.total_harga_transaksi.toString()
            Log.d("TabFrag", model.nama_pembeli)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_data_inventaris, parent, false)
            return ProductViewHolder(view)
        }
    }
}