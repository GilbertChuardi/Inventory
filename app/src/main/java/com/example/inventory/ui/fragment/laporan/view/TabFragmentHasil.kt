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

        db.collection("omset").document("omset")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result.data
                    data?.let {
                        for ((_, value) in data) {
                            val omset = value as Long
                            val convert = numberFormat1.format(omset.toInt())
                            binding.tvTotalOmset.text =
                                "Total omset : Rp " + convert.removeRange(0, 1)
                        }
                    }
                }
            }
    }
}