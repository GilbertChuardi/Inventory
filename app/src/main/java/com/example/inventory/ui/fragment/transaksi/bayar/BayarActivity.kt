package com.example.inventory.ui.fragment.transaksi.bayar

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
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
import com.example.inventory.databinding.ActivityBayarBinding
import com.example.inventory.model.DataModel
import com.example.inventory.ui.fragment.MainActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_data_bayar.view.*
import java.text.NumberFormat

class BayarActivity : AppCompatActivity(), View.OnClickListener {

    private var dataItem = ArrayList<String>()
    private var hargaBrg = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var jlhBeli = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var jlhBrg = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var namaBrg = arrayOf("", "", "", "", "", "", "", "", "", "")
    private lateinit var binding: ActivityBayarBinding
    private lateinit var adapter: ProductFirestoreRecyclerAdapter
    private var totalHarga: Int = 0
    private val numberFormat1: NumberFormat = NumberFormat.getCurrencyInstance()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBayarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        dataItem = intent.getStringArrayListExtra(EXTRA_DATA_BAYAR)!!
        binding.recyclerViewBayar.layoutManager = LinearLayoutManager(applicationContext)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("Inventaris").whereIn("id", dataItem)
        val options =
            FirestoreRecyclerOptions.Builder<DataModel>().setQuery(query, DataModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerViewBayar.adapter = adapter

        numberFormat1.maximumFractionDigits = 0
        binding.btnBackBayar.setOnClickListener(this)
        binding.btnLunasBayar.setOnClickListener(this)
        binding.btnBelumLunasBayar.setOnClickListener(this)
        binding.btnInputManualBayar.setOnClickListener(this)
        binding.btnHapusSemuaBayar.setOnClickListener(this)
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<DataModel>) :
        FirestoreRecyclerAdapter<DataModel, ProductViewHolder>(options) {

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: DataModel) {
            holder.setIsRecyclable(false)
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_bayar)
            val tvHargaBarang: TextView = holder.itemView.findViewById(R.id.tv_harga_barang_bayar)
            val tvJumlahBarang: TextView = holder.itemView.findViewById(R.id.tv_jumlah_barang_bayar)
            val btnDeleteBarang: ImageButton = holder.itemView.findViewById(R.id.btn_delete_bayar)
            val etBeliBarang: EditText = holder.itemView.findViewById(R.id.et_jumlah_barang_bayar)
            tvNamaBarang.text = model.nama_barang
            tvHargaBarang.text = "Rp. " + model.harga_barang.toString()
            tvJumlahBarang.text = "Stok: " + model.jumlah_barang.toString()
            if (jlhBeli[holder.adapterPosition] != 0) {
                etBeliBarang.text = Editable.Factory.getInstance()
                    .newEditable(jlhBeli[holder.adapterPosition].toString())
            }
            btnDeleteBarang.setOnClickListener(CustomOnItemClickListener(
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
                        }
                    }
                }
            ))
            namaBrg[holder.adapterPosition] = model.nama_barang
            val watcher1 = Watcher1()
            watcher1.updatePosition(holder.adapterPosition)
            watcher1.update(model.harga_barang, model.jumlah_barang)
            holder.itemView.et_jumlah_barang_bayar.addTextChangedListener(watcher1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_data_bayar, parent, false)
            return ProductViewHolder(view)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back_bayar -> finish()
            R.id.btn_lunas_bayar -> inputData("Lunas")
            R.id.btn_belum_lunas_bayar -> inputData("Piutang")
            R.id.btn_hapus_semua_bayar -> deleteFromBayar()
            R.id.btn_input_manual_bayar -> inputManual()
        }
    }

    private fun inputManual() {
        if (trimmingarray()) {
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.alertdialog_input_manual, null)
            val editText1 = dialogLayout.findViewById<EditText>(R.id.et_input_manual_nama_bayar)
            val editText2 = dialogLayout.findViewById<EditText>(R.id.et_input_manual_harga_bayar)
            val builder = AlertDialog.Builder(this)
                .setTitle("Total Harga")
                .setView(dialogLayout)
                .setPositiveButton("OK", null)
                .show()

            val mPositiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
            mPositiveButton.setOnClickListener {
                if (editText1.text.toString().isNotEmpty() && editText2.text.toString()
                        .isNotEmpty() && editText2.text.toString().toInt() > 0
                ) {
                    totalHarga = editText2.text.toString().toInt()
                    lunasBayar(editText1.text.toString())
                    builder.dismiss()
                } else {
                    Toast.makeText(this, "Data tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun inputData(tipe: String) {
        if (tipe == "Lunas" && trimmingarray()) {
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.alertdialog_nama_bayar, null)
            val editText1 = dialogLayout.findViewById<EditText>(R.id.et_nama_pembeli_bayar)
            val builder = AlertDialog.Builder(this)
                .setTitle("Nama Pembeli")
                .setView(dialogLayout)
                .setPositiveButton("OK", null)
                .show()

            val mPositiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
            mPositiveButton.setOnClickListener {
                if (editText1.text.toString().isNotEmpty()) {
                    lunasBayar(editText1.text.toString())
                    builder.dismiss()
                } else {
                    Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (tipe == "Piutang" && trimmingarray()) {
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.alertdialog_namaketerangan_bayar, null)
            val editText1 =
                dialogLayout.findViewById<EditText>(R.id.et_nama_pembeli_keterangan_bayar)
            val editText2 =
                dialogLayout.findViewById<EditText>(R.id.et_keterangan_pembeli_keterangan_bayar)
            val builder = AlertDialog.Builder(this)
                .setTitle("Tulis data")
                .setView(dialogLayout)
                .setPositiveButton("OK", null)
                .show()

            val mPositiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
            mPositiveButton.setOnClickListener {
                if (editText1.text.toString().isNotEmpty() && editText2.text.toString()
                        .isNotEmpty()
                ) {
                    belumLunasBayar(editText1.text.toString(), editText2.text.toString())
                    builder.dismiss()
                } else {
                    Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun lunasBayar(nama: String) {
        var i = 0
        val t = now()
        val ts = Timestamp(t.seconds, 0)

        while (i < dataItem.size) {
            db.collection("Inventaris")
                .document(dataItem[i])
                .update("jumlah_barang", jlhBrg[i] - jlhBeli[i])
            i++
        }

        db.collection("omset").document("omset")
            .update("omset", FieldValue.increment(totalHarga.toLong()))

        val data = hashMapOf(
            "data_nama_item" to namaBrg.toList(),
            "data_total_item" to jlhBeli.toList(),
            "tanggal" to ts,
            "total_harga" to totalHarga,
            "nama" to nama
        )
        db.collection("riwayat_penjualan")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("Bayar Activity", "Data Added dengan ID: ${documentReference.id}")
            }
        finish()
    }

    private fun belumLunasBayar(nama: String, keterangan: String) {
        var i = 0
        val t = now()
        val ts = Timestamp(t.seconds, 0)
        val id = db.collection("daftar_piutang").document().id

        while (i < dataItem.size) {
            db.collection("Inventaris")
                .document(dataItem[i])
                .update("jumlah_barang", jlhBrg[i] - jlhBeli[i])
            i++
        }

        val data = hashMapOf(
            "data_nama_item" to namaBrg.toList(),
            "data_total_item" to jlhBeli.toList(),
            "tanggal" to ts,
            "total_harga" to totalHarga,
            "nama" to nama,
            "keterangan" to keterangan,
            "id" to id
        )
        db.collection("daftar_piutang")
            .document(id)
            .set(data)
            .addOnSuccessListener {
                Log.d("Bayar Activity", "Data Added")
            }
        finish()
    }

    private fun deleteFromBayar() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    private inner class Watcher1 : TextWatcher {
        var position = 0

        fun updatePosition(positionExt: Int) {
            this.position = positionExt
        }

        fun update(a: Int, b: Int) {
            hargaBrg[position] = a
            jlhBrg[position] = b
        }

        override fun afterTextChanged(arg0: Editable) {
            totalHarga = 0
            if (arg0.toString() != "") {
                arg0.filters = arrayOf<InputFilter>(MinMaxFilter(1, jlhBrg[position]))
                jlhBeli[position] = arg0.toString().toInt()
                var i = 0
                while (i < jlhBeli.size) {
                    totalHarga += hargaBrg[i] * jlhBeli[i]
                    i++
                }
                val convert = numberFormat1.format(totalHarga)
                binding.tvTotalHarga.text = "Total Harga : Rp. " + convert.removeRange(0, 1)
            } else {
                jlhBeli[position] = 0
                var i = 0
                while (i < jlhBeli.size) {
                    totalHarga += hargaBrg[i] * jlhBeli[i]
                    i++
                }
                val convert = numberFormat1.format(totalHarga)
                binding.tvTotalHarga.text = "Total Harga : Rp. " + convert.removeRange(0, 1)
            }
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
        override fun onTextChanged(s: CharSequence, a: Int, b: Int, c: Int) {}
    }

    private inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dStart: Int,
            dEnd: Int
        ): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    private fun trimmingarray(): Boolean {
        var j = 0
        while (j < namaBrg.size) {
            if (namaBrg[j] == "") {
                val arrList = namaBrg.toMutableList()
                arrList.removeAt(j)
                namaBrg = arrList.toTypedArray()
                j--
            }
            j++
        }

        var k = jlhBeli.size - 1
        while (k > namaBrg.size - 1) {
            val arrList = jlhBeli.toMutableList()
            arrList.removeAt(k)
            jlhBeli = arrList.toTypedArray()
            k--
        }

        var z = 0
        while (z < namaBrg.size) {
            if (jlhBeli[z] == 0) {
                Toast.makeText(this, "Tidak boleh ada 0", Toast.LENGTH_SHORT).show()
                return false
            }
            if (jlhBeli[z] > jlhBrg[z]) {
                Toast.makeText(this, "Tidak bisa melebihi stok", Toast.LENGTH_SHORT).show()
                return false
            }
            z++
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        const val EXTRA_DATA_BAYAR = "extra_data_bayar"
    }
}