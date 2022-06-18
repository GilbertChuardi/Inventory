package com.example.inventory.ui.fragment.inventaris

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
import com.example.inventory.DataModel
import com.example.inventory.R
import com.example.inventory.databinding.FragmentInventarisBinding
import com.example.inventory.ui.fragment.inventaris.detail.DetailActivity
import com.example.inventory.ui.fragment.inventaris.tambah.TambahActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class InventarisFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentInventarisBinding? = null
    private val binding get() = _binding!!
    private var adapter: ProductFirestoreRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInventarisBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvInventaris
        textView.text = "Inventaris"

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTambah.setOnClickListener(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("Inventaris")
        val options =
            FirestoreRecyclerOptions.Builder<DataModel>().setQuery(query, DataModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter
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

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<DataModel>) :
        FirestoreRecyclerAdapter<DataModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: DataModel) {
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang)
            val tvKodeBarang: TextView = holder.itemView.findViewById(R.id.tv_kode_barang)
            val tvJumlahBarang: TextView = holder.itemView.findViewById(R.id.tv_jumlah_barang)
            val cvItem: CardView = holder.itemView.findViewById(R.id.cv_item_inventaris)
            tvNamaBarang.text = model.nama_barang
            tvKodeBarang.text = model.kode_barang
            tvJumlahBarang.text = model.jumlah_barang.toString()
            cvItem.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra(DetailActivity.EXTRA_DATA, model)
                        activity?.startActivity(intent)
                    }
                }
            ))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_data_inventaris, parent, false)
            return ProductViewHolder(view)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_tambah -> {
                startActivity(Intent(requireContext(), TambahActivity::class.java))
            }
        }
    }


}