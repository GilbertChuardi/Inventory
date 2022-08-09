package com.example.inventory.ui.fragment.pembelian.beli.penyesuaian

import android.content.Intent
import android.os.Bundle
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
import com.example.inventory.databinding.ActivityPenyesuaianPilihInvoiceBinding
import com.example.inventory.model.InvoiceModel
import com.example.inventory.ui.fragment.MainActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PenyesuaianPilihInvoiceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPenyesuaianPilihInvoiceBinding
    private lateinit var adapter: ProductFirestoreRecyclerAdapter
    private lateinit var db: FirebaseFirestore
    private var selectedItem: Int = -1
    private var checkedItem: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyesuaianPilihInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        val query = db.collection("invoice_pembelian_barang")
            .orderBy("tanggal_invoice", Query.Direction.DESCENDING)
            .whereEqualTo("pembayaran", "lunas")
        val options =
            FirestoreRecyclerOptions.Builder<InvoiceModel>()
                .setQuery(query, InvoiceModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter

        binding.btnLanjutPenyesuaianPilihInvoice.setOnClickListener(this)
        binding.btnBatalPenyesuaianPilihInvoice.setOnClickListener(this)
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<InvoiceModel>) :
        FirestoreRecyclerAdapter<InvoiceModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: InvoiceModel
        ) {
            holder.setIsRecyclable(false)
            val tvTanggal: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_transaksi)
            val tvTotal: TextView = holder.itemView.findViewById(R.id.tv_harga_barang_transaksi)
            val rbSupplier: RadioButton = holder.itemView.findViewById(R.id.rb_supplier)
            val tanggal = model.tanggal_invoice.toDate().toLocaleString().toString()
            tvTanggal.text = tanggal
            tvTotal.text = "Rp. " + model.total_pembelian.toString()
            if (selectedItem == position) {
                rbSupplier.isChecked = true
            } else if (selectedItem != position) {
                rbSupplier.isChecked = false
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
                        }
                        checkedItem = model.id
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
            R.id.btn_lanjut_penyesuaian_pilih_invoice -> next()
            R.id.btn_batal_penyesuaian_pilih_invoice -> {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }
    }

    private fun next() {
        if (selectedItem != -1) {
            val intent = Intent(this, PenyesuaianPilihBarangActivity::class.java)
            intent.putExtra(
                PenyesuaianPilihBarangActivity.EXTRA_DATA,
                checkedItem
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