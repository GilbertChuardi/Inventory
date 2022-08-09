package com.example.inventory.ui.fragment.pembelian.beli

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
import com.example.inventory.R
import com.example.inventory.databinding.FragmentPembelianBeliBarangBinding
import com.example.inventory.model.BarangModel
import com.example.inventory.ui.fragment.pembelian.beli.barang.PembelianPilihSupplierActivity
import com.example.inventory.ui.fragment.pembelian.beli.penyesuaian.PenyesuaianPilihInvoiceActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PembelianBeliBarangFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentPembelianBeliBarangBinding? = null
    private val binding get() = _binding!!
    private var adapter: ProductFirestoreRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembelianBeliBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("barang").orderBy("kode_barang", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<BarangModel>()
            .setQuery(query, BarangModel::class.java)
            .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter

        binding.btnPembelianBeliBarang.setOnClickListener(this)
        binding.btnPembelianPenyesuaian.setOnClickListener(this)
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<BarangModel>) :
        FirestoreRecyclerAdapter<BarangModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: BarangModel
        ) {
            val tvNamaPembeli: TextView =
                holder.itemView.findViewById(R.id.tv_nama_barang_transaksi)
            val tvKode: TextView = holder.itemView.findViewById(R.id.tv_harga_barang_transaksi)
            val tvJumlah: TextView = holder.itemView.findViewById(R.id.tv_jumlah_barang_transaksi)
            val cvItem: CardView = holder.itemView.findViewById(R.id.cv_item_transaksi)
            tvNamaPembeli.text = model.nama_barang
            tvKode.text = model.kode_barang
            tvJumlah.text = "Stok : " + model.jumlah_barang.toString() + " " + model.satuan_barang
            /*cvItem.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val intent = Intent(activity, DetailPembeliActivity::class.java)
                        intent.putExtra(DetailPembeliActivity.EXTRA_DATA, model)
                        activity?.startActivity(intent)
                    }
                }
            ))*/
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_data_pembelian, parent, false)
            return ProductViewHolder(view)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_pembelian_beli_barang -> startActivity(
                Intent(
                    context,
                    PembelianPilihSupplierActivity::class.java
                )
            )
            R.id.btn_pembelian_penyesuaian -> startActivity(
                Intent(
                    context,
                    PenyesuaianPilihInvoiceActivity::class.java
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