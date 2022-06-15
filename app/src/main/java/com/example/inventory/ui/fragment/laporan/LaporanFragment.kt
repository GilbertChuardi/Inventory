package com.example.inventory.ui.fragment.laporan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.inventory.databinding.FragmentLaporanBinding

class LaporanFragment : Fragment() {

    private var _binding: FragmentLaporanBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLaporanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvLaporan
        textView.text = "Laporan"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}