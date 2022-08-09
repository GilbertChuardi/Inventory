package com.example.inventory.ui.fragment.home.pembeli

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
import com.example.inventory.databinding.FragmentTabPembeliBinding
import com.example.inventory.model.PembeliModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TabFragmentPembeli : Fragment(), View.OnClickListener {

    private var _binding: FragmentTabPembeliBinding? = null
    private val binding get() = _binding!!
    private var adapter: ProductFirestoreRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabPembeliBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("customer").orderBy("kode_pembeli", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<PembeliModel>()
            .setQuery(query, PembeliModel::class.java)
            .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter

        binding.btnTambahPembeli.setOnClickListener(this)
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<PembeliModel>) :
        FirestoreRecyclerAdapter<PembeliModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: PembeliModel
        ) {
            val tvNamaPembeli: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_riwayat)
            val tvKode: TextView = holder.itemView.findViewById(R.id.tv_kode_barang_riwayat)
            val tvJumlah: TextView = holder.itemView.findViewById(R.id.tv_jumlah_barang_riwayat)
            val cvItem: CardView = holder.itemView.findViewById(R.id.cv_item_riwayat)
            tvNamaPembeli.text = model.nama_pembeli
            tvKode.text = model.kode_pembeli
            tvJumlah.text = ""
            cvItem.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val intent = Intent(activity, DetailPembeliActivity::class.java)
                        intent.putExtra(DetailPembeliActivity.EXTRA_DATA, model)
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
            R.id.btn_tambah_pembeli -> startActivity(
                Intent(
                    context,
                    TambahPembeliActivity::class.java
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