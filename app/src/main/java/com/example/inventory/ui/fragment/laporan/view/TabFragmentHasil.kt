package com.example.inventory.ui.fragment.laporan.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.inventory.databinding.FragmentTabHasilBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat

class TabFragmentHasil : Fragment() {

    private var totalProfit: Long = 0
    private var _binding: FragmentTabHasilBinding? = null
    private val binding get() = _binding!!
    private val numberFormat1: NumberFormat = NumberFormat.getCurrencyInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabHasilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = FirebaseFirestore.getInstance()

        numberFormat1.maximumFractionDigits = 0

        db.collection("transaksi")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    totalProfit += document["total_profit"].toString().toLong()
                }
                val convert = numberFormat1.format(totalProfit)
                binding.tvTotalPendapatan.text =
                    "Total Pendapatan : Rp. " + convert.removeRange(0, 1)
            }
    }
}