package com.example.inventory.ui.fragment.transaksi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.inventory.databinding.FragmentTransaksiBinding

class TransaksiFragment : Fragment() {

    private var _binding: FragmentTransaksiBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvTransaksi
        textView.text = "Transaksi"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}