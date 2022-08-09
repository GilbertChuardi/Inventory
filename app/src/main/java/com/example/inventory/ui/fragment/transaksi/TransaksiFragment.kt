package com.example.inventory.ui.fragment.transaksi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.CustomOnItemClickListener
import com.example.inventory.R
import com.example.inventory.databinding.FragmentTransaksiBinding
import com.example.inventory.model.BarangModel
import com.example.inventory.ui.fragment.transaksi.bayar.BayarActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.NumberFormat

class TransaksiFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductFirestoreRecyclerAdapter
    private val numberFormat1: NumberFormat = NumberFormat.getCurrencyInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView: TextView = binding.tvTransaksi
        textView.text = "Transaksi"

        binding.recyclerViewTransaksi.layoutManager = LinearLayoutManager(context)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("barang").orderBy("kode_barang", Query.Direction.ASCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<BarangModel>()
                .setQuery(query, BarangModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerViewTransaksi.adapter = adapter

        numberFormat1.maximumFractionDigits = 0

        binding.btnBayar.setOnClickListener(this)
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<BarangModel>) :
        FirestoreRecyclerAdapter<BarangModel, ProductViewHolder>(options) {
        var checkedItem = ArrayList<String>()
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: BarangModel
        ) {
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_transaksi)
            val tvHargaBarang: TextView =
                holder.itemView.findViewById(R.id.tv_harga_barang_transaksi)
            val tvJumlahBarang: TextView =
                holder.itemView.findViewById(R.id.tv_jumlah_barang_transaksi)
            val checkBox: CheckBox = holder.itemView.findViewById(R.id.cb_transaksi)

            tvNamaBarang.text = model.nama_barang

            val convert = numberFormat1.format(model.harga_jual)
            tvHargaBarang.text = "Rp. " + convert.removeRange(0, 1)
            tvJumlahBarang.text =
                "Stok: " + model.jumlah_barang.toString() + " " + model.satuan_barang

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
                    .inflate(R.layout.item_data_transaksi, parent, false)

            return ProductViewHolder(view)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_bayar -> {
                when {
                    adapter.checkedItem.size in 1..10 -> {
                        val intent = Intent(activity, BayarActivity::class.java)
                        intent.putStringArrayListExtra(
                            BayarActivity.EXTRA_DATA_BAYAR,
                            adapter.checkedItem
                        )
                        startActivity(intent)
                    }
                    adapter.checkedItem.size > 10 -> {
                        Toast.makeText(
                            context,
                            "Tidak bisa membeli lebih dari 10 item",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            context,
                            "Tidak ada barang yang di ceklist",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}