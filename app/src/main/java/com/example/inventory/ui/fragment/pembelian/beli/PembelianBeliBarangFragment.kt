package com.example.inventory.ui.fragment.pembelian.beli

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.CustomOnItemClickListener
import com.example.inventory.R
import com.example.inventory.databinding.FragmentPembelianBeliBarangBinding
import com.example.inventory.model.BarangModel
import com.example.inventory.ui.fragment.pembelian.beli.barang.PembelianPilihSupplierActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PembelianBeliBarangFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentPembelianBeliBarangBinding? = null
    private val binding get() = _binding!!
    private var adapter: ProductFirestoreRecyclerAdapter? = null
    private lateinit var db: FirebaseFirestore

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
        db = FirebaseFirestore.getInstance()
        val query = db.collection("barang").orderBy("nama_barang", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<BarangModel>()
            .setQuery(query, BarangModel::class.java)
            .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter

        binding.btnPembelianBeliBarang.setOnClickListener(this)
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
            tvKode.text = "HB: " + model.harga_beli + " HJ: " + model.harga_jual
            tvJumlah.text = "Stok : " + model.jumlah_barang.toString() + " " + model.satuan_barang
            cvItem.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val inflater = layoutInflater
                        val dialogLayout =
                            inflater.inflate(R.layout.alertdialog_input_beli_barang, null)
                        val editTextBeli =
                            dialogLayout.findViewById<EditText>(R.id.et_input_harga_beli_beli_barang)
                        val editTextJual =
                            dialogLayout.findViewById<EditText>(R.id.et_input_harga_jual_beli_barang)
                        val editTextJlh =
                            dialogLayout.findViewById<EditText>(R.id.et_input_jumlah_barang_beli_barang)
                        val editTextSatuan =
                            dialogLayout.findViewById<EditText>(R.id.et_input_satuan_barang_beli_barang)

                        editTextBeli.setText(model.harga_beli.toString())
                        editTextJual.setText(model.harga_jual.toString())
                        editTextJlh.setText(model.jumlah_barang.toString())
                        editTextSatuan.setText(model.satuan_barang)

                        val builder = AlertDialog.Builder(context)
                            .setTitle("Edit Data")
                            .setView(dialogLayout)
                            .setPositiveButton("OK", null)
                            .show()

                        val mPositiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
                        mPositiveButton.setOnClickListener {
                            if (editTextBeli.text.toString().isNotEmpty() &&
                                editTextJual.text.toString().isNotEmpty() &&
                                editTextJlh.text.toString().isNotEmpty() &&
                                editTextSatuan.text.toString().isNotEmpty() &&
                                editTextJlh.text.toString().toInt() > 0 &&
                                editTextJual.text.toString()
                                    .toInt() > editTextBeli.text.toString().toInt()
                            ) {
                                db.collection("barang")
                                    .document(model.id)
                                    .update(
                                        mapOf(
                                            "harga_beli" to editTextBeli.text.toString().toInt(),
                                            "harga_jual" to editTextJual.text.toString().toInt(),
                                            "jumlah_barang" to editTextJlh.text.toString().toInt(),
                                            "satuan_barang" to editTextSatuan.text.toString()
                                        )
                                    )
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Barang Updated",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                notifyDataSetChanged()
                                builder.dismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Data tidak valid",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            ))
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