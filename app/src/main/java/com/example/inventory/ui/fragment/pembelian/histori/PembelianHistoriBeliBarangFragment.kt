package com.example.inventory.ui.fragment.pembelian.histori

import android.app.AlertDialog
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
import com.example.inventory.databinding.FragmentPembelianHistoriBeliBarangBinding
import com.example.inventory.model.InvoiceModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.NumberFormat

class PembelianHistoriBeliBarangFragment : Fragment() {

    private var _binding: FragmentPembelianHistoriBeliBarangBinding? = null
    private val binding get() = _binding!!
    private var adapter: ProductFirestoreRecyclerAdapter? = null
    private val numberFormat1: NumberFormat = NumberFormat.getCurrencyInstance()
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembelianHistoriBeliBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        db = FirebaseFirestore.getInstance()
        val query =
            db.collection("invoice_pembelian_barang")
                .orderBy("tanggal_invoice", Query.Direction.DESCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<InvoiceModel>()
                .setQuery(query, InvoiceModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter

        numberFormat1.maximumFractionDigits = 0
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<InvoiceModel>) :
        FirestoreRecyclerAdapter<InvoiceModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: InvoiceModel
        ) {
            val tvNamaPembeli: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_riwayat)
            val tvTanggal: TextView = holder.itemView.findViewById(R.id.tv_kode_barang_riwayat)
            val tvTotalHarga: TextView = holder.itemView.findViewById(R.id.tv_jumlah_barang_riwayat)
            val cvItem: CardView = holder.itemView.findViewById(R.id.cv_item_riwayat)
            val tanggal = model.tanggal_invoice.toDate().toLocaleString().toString()
            val convert = numberFormat1.format(model.total_pembelian)
            tvNamaPembeli.text = tanggal.removeRange(0, 12)
            tvTanggal.text = tanggal.removeRange(12, tanggal.length)
            tvTotalHarga.text = ""
            cvItem.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val sb = StringBuilder()

                        db.collection("detail_invoice_pembelian_barang")
                            .whereEqualTo("invoice_id", model.id)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    sb.append(
                                        "- ",
                                        document["nama_barang"],
                                        " : ",
                                        document["jumlah_barang"],
                                        " \nHarga Beli: Rp.",
                                        document["harga_beli"],
                                        "\n"
                                    )
                                }
                                val inflater = layoutInflater
                                val dialogLayout =
                                    inflater.inflate(R.layout.alertdialog_detail_riwayat, null)
                                val tvDetailNama =
                                    dialogLayout.findViewById<TextView>(R.id.tv_detail_riwayat_nama)
                                val tvDetailData =
                                    dialogLayout.findViewById<TextView>(R.id.tv_detail_riwayat_data)
                                val tvDetailTanggal =
                                    dialogLayout.findViewById<TextView>(R.id.tv_detail_riwayat_tanggal)
                                dialogLayout.findViewById<TextView>(R.id.tv_detail_riwayat_total_harga)

                                tvDetailNama.text = "ID : " + model.id
                                tvDetailData.text = sb.toString()
                                tvDetailTanggal.text =
                                    model.tanggal_invoice.toDate().toLocaleString().toString()

                                AlertDialog.Builder(activity)
                                    .setTitle("Detail Riwayat Tambah")
                                    .setView(dialogLayout)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .show()
                            }
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