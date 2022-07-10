package com.example.inventory.ui.fragment.transaksi

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.inventory.TransaksiModel
import com.example.inventory.databinding.FragmentTransaksiBinding
import com.example.inventory.ui.fragment.transaksi.bayar.BayarActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class TransaksiFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductFirestoreRecyclerAdapter
    private var sb: StringBuilder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvTransaksi
        textView.text = "Transaksi"

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBayar.setOnClickListener(this)

        binding.recyclerViewTransaksi.layoutManager = LinearLayoutManager(context)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("Inventaris")
        val options =
            FirestoreRecyclerOptions.Builder<TransaksiModel>()
                .setQuery(query, TransaksiModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerViewTransaksi.adapter = adapter
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

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<TransaksiModel>) :
        FirestoreRecyclerAdapter<TransaksiModel, ProductViewHolder>(options) {
        var checkedItem = ArrayList<String>()
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: TransaksiModel
        ) {
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_transaksi)
            val tvHargaBarang: TextView =
                holder.itemView.findViewById(R.id.tv_harga_barang_transaksi)
            val tvJumlahBarang: TextView =
                holder.itemView.findViewById(R.id.tv_jumlah_barang_transaksi)
            val checkBox: CheckBox = holder.itemView.findViewById(R.id.cb_transaksi)

            tvNamaBarang.text = model.nama_barang
            tvHargaBarang.text = "Rp. " + model.harga_barang.toString()
            tvJumlahBarang.text = "Stok: " + model.jumlah_barang.toString()

            if (checkedItem.size != 0) {
                var i = 0
                var flag = false
                while (i < checkedItem.size) {
                    if (checkedItem[i] == model.id) {
                        Log.d("transaksi", "transaksi is true" + model.id)
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
                            Log.d("transaksi", "transaksi add" + model.id)
                        } else if (!myCheckBox.isChecked) {
                            model.isSelected = false
                            checkedItem.remove(model.id)
                            Log.d("transaksi", "transaksi remove " + model.id)
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
                if (adapter.checkedItem.size in 1..10) {
                    val intent = Intent(activity, BayarActivity::class.java)
                    intent.putStringArrayListExtra(
                        BayarActivity.EXTRA_DATA_BAYAR,
                        adapter.checkedItem
                    )
                    startActivity(intent)
                } else if (adapter.checkedItem.size > 10) {
                    Toast.makeText(
                        context,
                        "Tidak bisa membeli lebih dari 10 item",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, "Tidak ada barang yang di ceklist", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


}