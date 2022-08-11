package com.example.inventory.ui.fragment.pembelian.beli.barang

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.inventory.databinding.ActivityPembelianBeliBarangBinding
import com.example.inventory.model.SupplierBarangModel
import com.example.inventory.ui.fragment.MainActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PembelianBeliBarangActivity : AppCompatActivity(), View.OnClickListener {

    private var dataItem = ArrayList<String>()
    private var dataNama: String = ""
    private var supplierId: String = ""
    private var hargaBeli = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var hargaJual = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var jlhBrg = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var namaBrg = arrayOf("", "", "", "", "", "", "", "", "", "")
    private var satuanBrg = arrayOf("", "", "", "", "", "", "", "", "", "")
    private var totalHarga: Int = 0
    private lateinit var binding: ActivityPembelianBeliBarangBinding
    private lateinit var adapter: ProductFirestoreRecyclerAdapter
    private lateinit var db: FirebaseFirestore
    private var randomString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembelianBeliBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        dataItem = intent.getStringArrayListExtra(EXTRA_DATA)!!
        dataNama = intent.getStringExtra(EXTRA_DATA_NAMA)!!

        db = FirebaseFirestore.getInstance()

        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("supplier_barang").whereIn("id", dataItem)
        val options =
            FirestoreRecyclerOptions.Builder<SupplierBarangModel>()
                .setQuery(query, SupplierBarangModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter

        randomString = getRandomString()

        binding.btnPembelianBeliBarang.setOnClickListener(this)
        binding.btnBatalPembelianBeliBarang.setOnClickListener(this)
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<SupplierBarangModel>) :
        FirestoreRecyclerAdapter<SupplierBarangModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: SupplierBarangModel
        ) {
            holder.setIsRecyclable(false)
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_transaksi)
            val tvHargaBarang: TextView =
                holder.itemView.findViewById(R.id.tv_harga_barang_transaksi)
            val tvJlhBarang: TextView =
                holder.itemView.findViewById(R.id.tv_jumlah_barang_transaksi)
            val imgBtnEdit: ImageButton = holder.itemView.findViewById(R.id.img_btn_edit)
            val imgBtnDelete: ImageButton = holder.itemView.findViewById(R.id.img_btn_delete)

            tvNamaBarang.text = model.nama_barang
            supplierId = model.supplier_id
            tvHargaBarang.text =
                "HB: " + hargaBeli[position].toString() + " HJ:" + hargaJual[position].toString()
            tvJlhBarang.text = "Jumlah: " + jlhBrg[position].toString() + " " + satuanBrg[position]

            imgBtnEdit.setOnClickListener(
                CustomOnItemClickListener(
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

                            editTextBeli.setText(hargaBeli[position].toString())
                            editTextJual.setText(hargaJual[position].toString())
                            editTextJlh.setText(jlhBrg[position].toString())
                            editTextSatuan.setText(satuanBrg[position])

                            val builder = AlertDialog.Builder(this@PembelianBeliBarangActivity)
                                .setTitle("Input Data")
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
                                    hargaBeli[position] = editTextBeli.text.toString().toInt()
                                    hargaJual[position] = editTextJual.text.toString().toInt()
                                    jlhBrg[position] = editTextJlh.text.toString().toInt()
                                    satuanBrg[position] = editTextSatuan.text.toString()
                                    namaBrg[position] = model.nama_barang
                                    notifyDataSetChanged()
                                    builder.dismiss()
                                } else {
                                    Toast.makeText(
                                        this@PembelianBeliBarangActivity,
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
            R.id.btn_pembelian_beli_barang -> {
                if (trimmingarray()) {
                    createInvoice()
                    cekData()
                }
            }
            R.id.btn_batal_pembelian_beli_barang -> {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }
    }

    private fun cekData() {
        readData(object : MyCallback {
            override fun onCallback(value: String, position: Int) {
                if (value == "ada") {
                    updateData(position)
                } else if (value == "kosong") {
                    createData(position)
                }
            }
        })
    }

    private fun readData(myCallback: MyCallback) {
        for (i in namaBrg.indices) {
            Log.d("Tag", "i = $i")
            Log.d("Tag", "masuk while")
            val cekBrg = db.collection("barang").whereEqualTo("nama_barang", namaBrg[i])
            cekBrg.get()
                .addOnCompleteListener { task ->
                    Log.d("Tag", "query sudah siap")
                    if (task.isSuccessful) {
                        var data = "kosong"
                        for (document in task.result) {
                            data = "ada"
                        }
                        myCallback.onCallback(data, i)
                    }
                }
        }
    }

    private fun createInvoice() {
        val t = now()
        val ts = Timestamp(t.seconds, 0)

        val dataInvoice = hashMapOf(
            "supplier_id" to supplierId,
            "tanggal_invoice" to ts,
            "total_pembelian" to totalHarga,
            "id" to "INV$randomString"
        )

        db.collection("invoice_pembelian_barang")
            .document("INV$randomString")
            .set(dataInvoice)
            .addOnSuccessListener {
                Log.d("Tag", "invoice sudah ditambah")
            }
    }

    private fun createData(i: Int) {
        Log.d("TAG", i.toString())
        val id = db.collection("barang").document().id
        val dataBarang = hashMapOf(
            "harga_beli" to hargaBeli[i],
            "harga_jual" to hargaJual[i],
            "id" to id,
            "jumlah_barang" to jlhBrg[i],
            "nama_barang" to namaBrg[i],
            "nama_supplier" to dataNama,
            "satuan_barang" to satuanBrg[i],
            "supplier_id" to supplierId
        )

        db.collection("barang")
            .document(id)
            .set(dataBarang)
            .addOnSuccessListener {
                Toast.makeText(this, "Barang Added", Toast.LENGTH_SHORT).show()
                Log.d("Tag", "barang sudah ditambah")
            }

        val idDetail = db.collection("detail_invoice_pembelian_barang").document().id
        val dataDetailInvoice = hashMapOf(
            "barang_id" to id,
            "harga_beli" to hargaBeli[i],
            "id" to idDetail,
            "invoice_id" to "INV$randomString",
            "jumlah_barang" to jlhBrg[i],
            "nama_barang" to namaBrg[i],
            "nama_supplier" to dataNama,
            "supplier_id" to supplierId
        )

        db.collection("detail_invoice_pembelian_barang")
            .document(idDetail)
            .set(dataDetailInvoice)
            .addOnSuccessListener {
                Log.d("Tag", "detail invoice sudah ditambah")
            }

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun updateData(i: Int) {
        db.collection("barang")
            .whereEqualTo("nama_barang", namaBrg[i])
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val data = db.collection("barang").document(document.id)

                    data.update("jumlah_barang", FieldValue.increment(jlhBrg[i].toLong()))

                    val id = db.collection("detail_invoice_pembelian_barang").document().id
                    val dataDetailInvoice = hashMapOf(
                        "barang_id" to document.id,
                        "harga_beli" to hargaBeli[i],
                        "id" to id,
                        "invoice_id" to "INV$randomString",
                        "jumlah_barang" to jlhBrg[i],
                        "nama_barang" to namaBrg[i],
                        "nama_supplier" to dataNama,
                        "supplier_id" to supplierId
                    )

                    db.collection("detail_invoice_pembelian_barang")
                        .document(id)
                        .set(dataDetailInvoice)
                        .addOnSuccessListener {
                            Log.d("Tag", "detail invoice sudah ditambah")
                            Toast.makeText(this, "Barang Updated", Toast.LENGTH_SHORT).show()
                        }

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
    }

    interface MyCallback {
        fun onCallback(value: String, position: Int)
    }

    private fun getRandomString(): String {
        val charset = ('a'..'z')
        return (1..5)
            .map { charset.random() }
            .joinToString("")
    }

    private fun trimmingarray(): Boolean {
        var k = namaBrg.size - 1
        while (k > dataItem.size - 1) {
            val arrList = namaBrg.toMutableList()
            arrList.removeAt(k)
            namaBrg = arrList.toTypedArray()
            k--
        }

        k = hargaBeli.size - 1
        while (k > dataItem.size - 1) {
            val arrList = hargaBeli.toMutableList()
            arrList.removeAt(k)
            hargaBeli = arrList.toTypedArray()
            k--
        }

        k = hargaJual.size - 1
        while (k > dataItem.size - 1) {
            val arrList = hargaJual.toMutableList()
            arrList.removeAt(k)
            hargaJual = arrList.toTypedArray()
            k--
        }

        k = jlhBrg.size - 1
        while (k > dataItem.size - 1) {
            val arrList = jlhBrg.toMutableList()
            arrList.removeAt(k)
            jlhBrg = arrList.toTypedArray()
            k--
        }

        k = satuanBrg.size - 1
        while (k > dataItem.size - 1) {
            val arrList = satuanBrg.toMutableList()
            arrList.removeAt(k)
            satuanBrg = arrList.toTypedArray()
            k--
        }

        var z = 0
        while (z < namaBrg.size) {
            if (namaBrg[z] == "") {
                Toast.makeText(this, "Ada data kosong", Toast.LENGTH_SHORT).show()
                return false
            }
            z++
        }
        z = 0
        while (z < namaBrg.size) {
            totalHarga += hargaBeli[z] * jlhBrg[z]
            z++
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
        const val EXTRA_DATA_NAMA = "extra_data_nama"
    }
}