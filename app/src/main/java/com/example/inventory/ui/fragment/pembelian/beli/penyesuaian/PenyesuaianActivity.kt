package com.example.inventory.ui.fragment.pembelian.beli.penyesuaian

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.CustomOnItemClickListener
import com.example.inventory.R
import com.example.inventory.databinding.ActivityPenyesuaianBinding
import com.example.inventory.model.DetailInvoiceModel
import com.example.inventory.ui.fragment.MainActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PenyesuaianActivity : AppCompatActivity(), View.OnClickListener {

    private var metodeBayarList = arrayOf("lunas", "hutang")
    private var metodeBayar: String = "lunas"
    private var invoiceId: String = ""
    private var dataItem = ArrayList<String>()
    private var jlhBrgAwal = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var jlhBrgAkhir = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var barangId = arrayOf("", "", "", "", "", "", "", "", "", "")
    private lateinit var binding: ActivityPenyesuaianBinding
    private lateinit var adapter: ProductFirestoreRecyclerAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyesuaianBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        dataItem = intent.getStringArrayListExtra(EXTRA_DATA)!!
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("detail_invoice_pembelian_barang").whereIn("id", dataItem)
        val options =
            FirestoreRecyclerOptions.Builder<DetailInvoiceModel>()
                .setQuery(query, DetailInvoiceModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter
        binding.btnPenyesuaian.setOnClickListener(this)
        binding.btnBatalPenyesuaian.setOnClickListener(this)
        //showSpinner()
    }

    /*private fun showSpinner() {
        val spinner = binding.spinnerPenyesuaian
        val adapter = applicationContext?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_dropdown_item, metodeBayarList
            )
        }
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                metodeBayar = metodeBayarList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }*/

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<DetailInvoiceModel>) :
        FirestoreRecyclerAdapter<DetailInvoiceModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: DetailInvoiceModel
        ) {
            holder.setIsRecyclable(false)
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_transaksi)
            val tvKodeBarang: TextView =
                holder.itemView.findViewById(R.id.tv_harga_barang_transaksi)
            val tvJlhBarang: TextView =
                holder.itemView.findViewById(R.id.tv_jumlah_barang_transaksi)
            val imgBtnEdit: ImageButton = holder.itemView.findViewById(R.id.img_btn_edit)
            val imgBtnDelete: ImageButton = holder.itemView.findViewById(R.id.img_btn_delete)
            tvNamaBarang.text = model.nama_barang
            tvKodeBarang.text = "Jumlah beli lama: " + model.jumlah_barang
            jlhBrgAwal[position] = model.jumlah_barang
            barangId[position] = model.barang_id
            invoiceId = model.invoice_id
            tvJlhBarang.text = "Jumlah beli baru: " + jlhBrgAkhir[position].toString()

            imgBtnEdit.setOnClickListener(
                CustomOnItemClickListener(
                    position,
                    object : CustomOnItemClickListener.OnItemClickCallback {
                        override fun onItemClicked(view: View, position: Int) {
                            val inflater = layoutInflater
                            val dialogLayout =
                                inflater.inflate(R.layout.alertdialog_input_jumlah_barang, null)
                            val editTextBeli =
                                dialogLayout.findViewById<EditText>(R.id.et_input_jumlah_barang_penyesuaian)

                            editTextBeli.setText(jlhBrgAkhir[position].toString())

                            val builder = AlertDialog.Builder(this@PenyesuaianActivity)
                                .setTitle("Input Data")
                                .setView(dialogLayout)
                                .setPositiveButton("OK", null)
                                .show()

                            val mPositiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
                            mPositiveButton.setOnClickListener {
                                if (editTextBeli.text.toString()
                                        .isNotEmpty() && editTextBeli.text.toString() != "0"
                                ) {
                                    jlhBrgAkhir[position] = editTextBeli.text.toString().toInt()
                                    notifyDataSetChanged()
                                    builder.dismiss()
                                } else {
                                    Toast.makeText(
                                        this@PenyesuaianActivity,
                                        "Data tidak valid",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    })
            )

            imgBtnDelete.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        if (dataItem.size > 1) {
                            dataItem.remove(model.id)
                            finish()
                            overridePendingTransition(0, 0)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                        } else if (dataItem.size == 1) {
                            finish()
                            overridePendingTransition(0, 0)
                        }
                    }
                }
            ))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_data_beli_barang, parent, false)
            return ProductViewHolder(view)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_penyesuaian ->
                if (trimmingarray()) {
                    updateData()
                }
            R.id.btn_batal_penyesuaian -> {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }
    }

    private fun updateData() {
        for (i in 0 until dataItem.size) {
            val data = db.collection("barang").document(barangId[i])
            val beda = jlhBrgAkhir[i] - jlhBrgAwal[i]

            data.update("jumlah_barang", FieldValue.increment(beda.toLong()))
        }

        Toast.makeText(this, "Barang Updated", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun trimmingarray(): Boolean {
        var k = jlhBrgAwal.size - 1
        while (k > dataItem.size - 1) {
            val arrList = jlhBrgAwal.toMutableList()
            arrList.removeAt(k)
            jlhBrgAwal = arrList.toTypedArray()
            k--
        }

        k = jlhBrgAkhir.size - 1
        while (k > dataItem.size - 1) {
            val arrList = jlhBrgAkhir.toMutableList()
            arrList.removeAt(k)
            jlhBrgAkhir = arrList.toTypedArray()
            k--
        }

        k = barangId.size - 1
        while (k > dataItem.size - 1) {
            val arrList = barangId.toMutableList()
            arrList.removeAt(k)
            barangId = arrList.toTypedArray()
            k--
        }

        k = 0
        while (k < jlhBrgAkhir.size) {
            if (jlhBrgAkhir[k] == 0) {
                Toast.makeText(this, "Ada data kosong", Toast.LENGTH_SHORT).show()
                return false
            }
            k++
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}