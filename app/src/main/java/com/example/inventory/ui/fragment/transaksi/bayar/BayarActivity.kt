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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.CustomOnItemClickListener
import com.example.inventory.R
import com.example.inventory.databinding.ActivityBayarBinding
import com.example.inventory.model.BarangModel
import com.example.inventory.ui.fragment.MainActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_data_bayar.view.*
import java.text.NumberFormat

class BayarActivity : AppCompatActivity(), View.OnClickListener {

    private var dataItem = ArrayList<String>()
    private var hargaJual = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var hargaBeli = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var jlhJual = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var jlhBrg = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var namaBrg = arrayOf("", "", "", "", "", "", "", "", "", "")
    private var satuanBrg = arrayOf("", "", "", "", "", "", "", "", "", "")
    private val dataNama = ArrayList<String>()
    private var namaPembeli: String = ""
    private lateinit var binding: ActivityBayarBinding
    private lateinit var adapter: ProductFirestoreRecyclerAdapter
    private var totalHarga: Int = 0
    private var totalProfit: Int = 0
    private val numberFormat1: NumberFormat = NumberFormat.getCurrencyInstance()
    private lateinit var db: FirebaseFirestore
    private var randomString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBayarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        dataItem = intent.getStringArrayListExtra(EXTRA_DATA_BAYAR)!!

        db = FirebaseFirestore.getInstance()
        binding.recyclerViewBayar.layoutManager = LinearLayoutManager(applicationContext)
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("barang").whereIn("id", dataItem)
            .orderBy("nama_barang", Query.Direction.ASCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<BarangModel>().setQuery(query, BarangModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerViewBayar.adapter = adapter

        randomString = getRandomString()
        spinnerPembeli()
        numberFormat1.maximumFractionDigits = 0

        binding.btnBackBayar.setOnClickListener(this)
        binding.btnLunasBayar.setOnClickListener(this)
        binding.btnBelumLunasBayar.setOnClickListener(this)
        binding.btnInputManualBayar.setOnClickListener(this)
        binding.btnHapusSemuaBayar.setOnClickListener(this)
    }

    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<BarangModel>) :
        FirestoreRecyclerAdapter<BarangModel, ProductViewHolder>(options) {

        override fun onBindViewHolder(
            holder: ProductViewHolder,
            position: Int,
            model: BarangModel
        ) {
            holder.setIsRecyclable(false)
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_bayar)
            val tvHargaBarang: TextView = holder.itemView.findViewById(R.id.tv_harga_barang_bayar)
            val tvJumlahBarang: TextView = holder.itemView.findViewById(R.id.tv_jumlah_barang_bayar)
            val btnDeleteBarang: ImageButton = holder.itemView.findViewById(R.id.btn_delete_bayar)
            val etBeliBarang: EditText = holder.itemView.findViewById(R.id.et_jumlah_barang_bayar)
            tvNamaBarang.text = model.nama_barang

            val convert = numberFormat1.format(model.harga_jual)
            tvHargaBarang.text = "Rp. " + convert.removeRange(0, 1)
            tvJumlahBarang.text =
                "Stok: " + model.jumlah_barang.toString() + " " + model.satuan_barang
            if (jlhJual[holder.adapterPosition] != 0) {
                etBeliBarang.text = Editable.Factory.getInstance()
                    .newEditable(jlhJual[holder.adapterPosition].toString())
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
            satuanBrg[holder.adapterPosition] = model.satuan_barang
            val watcher1 = Watcher1()
            watcher1.updatePosition(holder.adapterPosition)
            watcher1.update(model.harga_jual, model.jumlah_barang, model.harga_beli)
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
            R.id.btn_input_manual_bayar -> inputData("Manual")
            R.id.btn_hapus_semua_bayar -> deleteFromBayar()
        }
    }

    private fun inputData(tipe: String) {
        if (tipe == "Lunas" && trimmingarray()) {
            showSpinnerLunas()
        } else if (tipe == "Piutang" && trimmingarray()) {
            showSpinnerBelumLunas()
        } else if (tipe == "Manual" && trimmingarray()) {
            showSpinnerManual()
        }
    }

    // untuk lunas
    // untuk lunas
    // untuk lunas

    private fun showSpinnerLunas() {
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alertdialog_nama_bayar, null)
        val spinner1 = dialogLayout.findViewById<Spinner>(R.id.spinner_nama_pembeli)
        val adapter = applicationContext?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_dropdown_item, dataNama
            )
        }
        spinner1.adapter = adapter
        spinner1.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                namaPembeli = dataNama[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val builder = AlertDialog.Builder(this)
            .setTitle("Nama Pembeli")
            .setView(dialogLayout)
            .setPositiveButton("OK", null)
            .show()

        val mPositiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            createInvoiceLunas()
            createDataLunas()
        }
    }

    private fun createInvoiceLunas() {
        val t = now()
        val ts = Timestamp(t.seconds, 0)

        val dataInvoice = hashMapOf(
            "nama_pembeli" to namaPembeli,
            "tanggal_penjualan" to ts,
            "total_harga" to totalHarga,
            "total_profit" to totalProfit,
            "transaksi_penjualan_id" to "PEN$randomString"
        )

        db.collection("transaksi")
            .document()
            .set(dataInvoice)
            .addOnSuccessListener {
                Log.d("Tag", "invoice transaksi sudah ditambah")
            }
    }

    private fun createDataLunas() {
        for (i in namaBrg.indices) {
            val data = hashMapOf(
                "id" to "PEN$randomString",
                "jumlah_jual" to jlhJual[i],
                "nama_barang" to namaBrg[i],
                "satuan_barang" to satuanBrg[i]
            )

            db.collection("transaksi_penjualan")
                .document()
                .set(data)

            db.collection("barang")
                .document(dataItem[i])
                .update("jumlah_barang", jlhBrg[i] - jlhJual[i])
        }
        Toast.makeText(this, "Transaksi Berhasil", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    //utk blm lunas
    //utk blm lunas
    //utk blm lunas

    private fun showSpinnerBelumLunas() {
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alertdialog_namaketerangan_bayar, null)
        val spinner1 =
            dialogLayout.findViewById<Spinner>(R.id.spinner_nama_pembeli_keterangan_bayar)
        val editText1 =
            dialogLayout.findViewById<EditText>(R.id.et_keterangan_pembeli_keterangan_bayar)
        val adapter = applicationContext?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_dropdown_item, dataNama
            )
        }
        spinner1.adapter = adapter
        spinner1.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                namaPembeli = dataNama[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val builder = AlertDialog.Builder(this)
            .setTitle("Nama Pembeli & Keterangan")
            .setView(dialogLayout)
            .setPositiveButton("OK", null)
            .show()

        val mPositiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            if (editText1.text.isNotEmpty()) {
                createInvoiceBelumLunas(editText1.text.toString())
                createDataBelumLunas()
            }
        }
    }

    private fun createInvoiceBelumLunas(keterangan: String) {
        val t = now()
        val ts = Timestamp(t.seconds, 0)
        val id = db.collection("transaksip").document().id

        val dataInvoice = hashMapOf(
            "id" to id,
            "keterangan" to keterangan,
            "nama_pembeli" to namaPembeli,
            "tanggal_penjualan" to ts,
            "total_harga" to totalHarga,
            "total_profit" to totalProfit,
            "transaksi_penjualan_id" to "PEN$randomString"
        )

        db.collection("transaksip")
            .document(id)
            .set(dataInvoice)
            .addOnSuccessListener {
                Log.d("Tag", "invoice transaksi sudah ditambah")
            }
    }

    private fun createDataBelumLunas() {
        val id = db.collection("transaksip_penjualan").document().id
        for (i in namaBrg.indices) {
            val data = hashMapOf(
                "id" to "PEN$randomString",
                "jumlah_jual" to jlhJual[i],
                "nama_barang" to namaBrg[i],
                "penjualan_id" to id,
                "satuan_barang" to satuanBrg[i]
            )

            db.collection("transaksip_penjualan")
                .document()
                .set(data)

            db.collection("barang")
                .document(dataItem[i])
                .update("jumlah_barang", jlhBrg[i] - jlhJual[i])
        }
        Toast.makeText(this, "Transaksi Piutang Berhasil", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    //utk manual
    //utk manual
    //utk manual

    private fun showSpinnerManual() {
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alertdialog_input_manual, null)
        val spinner1 = dialogLayout.findViewById<Spinner>(R.id.spinner_input_manual_nama_bayar)
        val editText1 = dialogLayout.findViewById<EditText>(R.id.et_input_manual_harga_bayar)
        val adapter = applicationContext?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_dropdown_item, dataNama
            )
        }
        spinner1.adapter = adapter
        spinner1.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                namaPembeli = dataNama[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val builder = AlertDialog.Builder(this)
            .setTitle("Nama Pembeli & Harga")
            .setView(dialogLayout)
            .setPositiveButton("OK", null)
            .show()

        val mPositiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            if (editText1.text.isNotEmpty()) {
                createInvoiceManual(editText1.text.toString())
                createDataManual()
            }
        }
    }

    private fun createInvoiceManual(totalHargaManual: String) {
        val t = now()
        val ts = Timestamp(t.seconds, 0)

        totalProfit -= (totalHarga - totalHargaManual.toInt())

        val dataInvoice = hashMapOf(
            "nama_pembeli" to namaPembeli,
            "tanggal_penjualan" to ts,
            "total_harga" to totalHargaManual.toInt(),
            "total_profit" to totalProfit,
            "transaksi_penjualan_id" to "PEN$randomString"
        )

        db.collection("transaksi")
            .document()
            .set(dataInvoice)
            .addOnSuccessListener {
                Log.d("Tag", "invoice transaksi sudah ditambah")
            }
    }

    private fun createDataManual() {
        for (i in namaBrg.indices) {
            val data = hashMapOf(
                "id" to "PEN$randomString",
                "jumlah_jual" to jlhJual[i],
                "nama_barang" to namaBrg[i],
                "satuan_barang" to satuanBrg[i]
            )

            db.collection("transaksi_penjualan")
                .document()
                .set(data)

            db.collection("barang")
                .document(dataItem[i])
                .update("jumlah_barang", jlhBrg[i] - jlhJual[i])
        }
        Toast.makeText(this, "Transaksi Manual Berhasil", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    private fun spinnerPembeli() {
        db.collection("customer")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    dataNama.add(document["nama_pembeli"].toString())
                }
            }
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

        fun update(a: Int, b: Int, c: Int) {
            hargaJual[position] = a
            jlhBrg[position] = b
            hargaBeli[position] = c
        }

        override fun afterTextChanged(arg0: Editable) {
            totalHarga = 0
            totalProfit = 0
            if (arg0.toString() != "") {
                arg0.filters = arrayOf<InputFilter>(MinMaxFilter(1, jlhBrg[position]))
                jlhJual[position] = arg0.toString().toInt()
                var i = 0
                while (i < jlhJual.size) {
                    totalHarga += hargaJual[i] * jlhJual[i]
                    totalProfit += (hargaJual[i] - hargaBeli[i]) * jlhJual[i]
                    i++
                }
                val convert = numberFormat1.format(totalHarga)
                binding.tvTotalHarga.text = "Total Harga : Rp. " + convert.removeRange(0, 1)
            } else {
                jlhJual[position] = 0
                var i = 0
                while (i < jlhJual.size) {
                    totalHarga += hargaJual[i] * jlhJual[i]
                    totalProfit += (hargaJual[i] - hargaBeli[i]) * jlhJual[i]
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

        var k = jlhJual.size - 1
        while (k > namaBrg.size - 1) {
            val arrList = jlhJual.toMutableList()
            arrList.removeAt(k)
            jlhJual = arrList.toTypedArray()
            k--
        }

        k = satuanBrg.size - 1
        while (k > namaBrg.size - 1) {
            val arrList = satuanBrg.toMutableList()
            arrList.removeAt(k)
            satuanBrg = arrList.toTypedArray()
            k--
        }

        var z = 0
        while (z < namaBrg.size) {
            if (jlhJual[z] == 0) {
                Toast.makeText(this, "Tidak boleh ada 0", Toast.LENGTH_SHORT).show()
                return false
            }
            if (jlhJual[z] > jlhBrg[z]) {
                Toast.makeText(this, "Tidak bisa melebihi stok", Toast.LENGTH_SHORT).show()
                return false
            }
            z++
        }
        return true
    }

    private fun getRandomString(): String {
        val charset = ('a'..'z')
        return (1..5)
            .map { charset.random() }
            .joinToString("")
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