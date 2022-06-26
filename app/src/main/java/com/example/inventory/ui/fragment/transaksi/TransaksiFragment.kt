package com.example.inventory.ui.fragment.transaksi

import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.DataModel
import com.example.inventory.R
import com.example.inventory.databinding.FragmentTransaksiBinding
import com.example.inventory.databinding.ItemDataTransaksiBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class TransaksiFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentTransaksiBinding? = null
    private var _binding1: ItemDataTransaksiBinding? = null
    private val binding get() = _binding!!
    private var adapter: ProductFirestoreRecyclerAdapter? = null
    val checkBoxStatesArray = SparseBooleanArray()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        val root: View = binding.root
        _binding1 = ItemDataTransaksiBinding.inflate(inflater,container,false)

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
            FirestoreRecyclerOptions.Builder<DataModel>().setQuery(query, DataModel::class.java)
                .build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerViewTransaksi.adapter = adapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view){
        init {
            Log.d("transaksi masuk cek",adapterPosition.toString())
            val checkbox = _binding1?.cbTransaksi
            /*checkbox?.setOnClickListener {
                if(!checkBoxStatesArray.get(adapterPosition, false)){
                    checkbox.isChecked = true
                    checkBoxStatesArray.put(adapterPosition, true)
                }else{
                    checkbox.isChecked = false
                    checkBoxStatesArray.put(adapterPosition, false)
                }
            }*/

            checkbox?.setOnCheckedChangeListener { _, isChecked ->
                Log.d("transaksi cek clicked",adapterPosition.toString())
                checkBoxStatesArray.put(adapterPosition, isChecked)
            }
        }
    }



    private inner class ProductFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<DataModel>) :
        FirestoreRecyclerAdapter<DataModel, ProductViewHolder>(options) {
        override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: DataModel) {
            val tvNamaBarang: TextView = holder.itemView.findViewById(R.id.tv_nama_barang_transaksi)
            val tvHargaBarang: TextView = holder.itemView.findViewById(R.id.tv_harga_barang_transaksi)
            val tvJumlahBarang: TextView = holder.itemView.findViewById(R.id.tv_jumlah_barang_transaksi)
            val cbTransaksi: CheckBox = holder.itemView.findViewById(R.id.cb_transaksi)
            val cvItem: CardView = holder.itemView.findViewById(R.id.cv_item_transaksi)

            cbTransaksi.isChecked = checkBoxStatesArray.get(position, false)
            tvNamaBarang.text = model.nama_barang
            tvHargaBarang.text = model.kode_barang
            tvJumlahBarang.text = model.jumlah_barang.toString()
            /*cvItem.setOnClickListener(CustomOnItemClickListener(
                position,
                object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra(DetailActivity.EXTRA_DATA, model)
                        activity?.startActivity(intent)
                    }
                }
            ))*/
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_data_transaksi, parent, false)

            return ProductViewHolder(view)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_bayar -> {
                Log.d("transaksi yang ada",checkBoxStatesArray[0].toString())
                Log.d("transaksi yang ada",checkBoxStatesArray[1].toString())
                Log.d("transaksi yang ada",checkBoxStatesArray[2].toString())
                Log.d("transaksi yang ada",checkBoxStatesArray[3].toString())
            }
        }
    }

}