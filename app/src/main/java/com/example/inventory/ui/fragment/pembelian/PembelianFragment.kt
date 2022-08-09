package com.example.inventory.ui.fragment.pembelian

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.inventory.R
import com.example.inventory.databinding.FragmentPembelianBinding
import com.example.inventory.ui.fragment.pembelian.beli.PembelianBeliBarangFragment
import com.example.inventory.ui.fragment.pembelian.histori.PembelianHistoriBeliBarangFragment
import com.google.android.material.tabs.TabLayout

class PembelianFragment : Fragment() {

    private var _binding: FragmentPembelianBinding? = null
    private val binding get() = _binding!!
    private var tabLayout: TabLayout? = null
    private var frameLayout: FrameLayout? = null
    private var fragmentManager1: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null
    var fragment: Fragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembelianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = binding.tabLayout
        frameLayout = binding.frameLayout

        fragment = PembelianBeliBarangFragment()
        fragmentManager1 = childFragmentManager
        fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction!!.replace(R.id.frame_layout, fragment as PembelianBeliBarangFragment)
        fragmentTransaction!!.commit()

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> fragment = PembelianBeliBarangFragment()
                    1 -> fragment = PembelianHistoriBeliBarangFragment()
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