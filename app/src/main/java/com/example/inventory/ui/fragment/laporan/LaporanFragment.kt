package com.example.inventory.ui.fragment.laporan


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.inventory.R
import com.example.inventory.databinding.FragmentLaporanBinding
import com.example.inventory.ui.fragment.laporan.view.TabFragmentHasil
import com.example.inventory.ui.fragment.laporan.view.TabFragmentRiwayat
import com.example.inventory.ui.fragment.laporan.view.daftar.TabFragmentDaftar
import com.google.android.material.tabs.TabLayout

class LaporanFragment : Fragment() {

    private var _binding: FragmentLaporanBinding? = null
    private val binding get() = _binding!!
    private var tabLayout: TabLayout? = null
    private var frameLayout: FrameLayout? = null
    var fragment: Fragment? = null
    private var fragmentManager1: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaporanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView: TextView = binding.tvLaporan
        textView.text = "Laporan"

        tabLayout = binding.tabLayout
        frameLayout = binding.frameLayout

        fragment = TabFragmentHasil()
        fragmentManager1 = childFragmentManager
        fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction!!.replace(R.id.frame_layout, fragment as TabFragmentHasil)
        fragmentTransaction!!.commit()

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> fragment = TabFragmentHasil()
                    1 -> fragment = TabFragmentRiwayat()
                    2 -> fragment = TabFragmentDaftar()

                }
                fragmentTransaction = requireFragmentManager().beginTransaction()
                fragmentTransaction!!.replace(R.id.frame_layout, fragment!!)
                fragmentTransaction!!.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}