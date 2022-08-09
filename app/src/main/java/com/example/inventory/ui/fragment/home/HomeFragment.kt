package com.example.inventory.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.inventory.R
import com.example.inventory.databinding.FragmentHomeBinding
import com.example.inventory.ui.fragment.home.pembeli.TabFragmentPembeli
import com.example.inventory.ui.fragment.home.supplier.TabFragmentSupplier
import com.example.inventory.ui.fragment.home.supplier_barang.TabFragmentSupplierBarang
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var tabLayout: TabLayout? = null
    private var frameLayout: FrameLayout? = null
    private var fragmentManager1: FragmentManager? = null
    var fragment: Fragment? = null
    var fragmentTransaction: FragmentTransaction? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = binding.tabLayout
        frameLayout = binding.frameLayout

        fragment = TabFragmentSupplier()
        fragmentManager1 = childFragmentManager
        fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction!!.replace(R.id.frame_layout, fragment as TabFragmentSupplier)
        fragmentTransaction!!.commit()

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> fragment = TabFragmentSupplier()
                    1 -> fragment = TabFragmentSupplierBarang()
                    2 -> fragment = TabFragmentPembeli()
                }
                fragmentTransaction = childFragmentManager.beginTransaction()
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