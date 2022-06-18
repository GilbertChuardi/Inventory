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
import com.example.inventory.ui.fragment.laporan.view.TabFragmentDaftar
import com.example.inventory.ui.fragment.laporan.view.TabFragmentHasil
import com.example.inventory.ui.fragment.laporan.view.TabFragmentRiwayat
import com.google.android.material.tabs.TabLayout

class LaporanFragment : Fragment() {

    private var _binding: FragmentLaporanBinding? = null

    private val binding get() = _binding!!

    var tabLayout: TabLayout? = null
    var frameLayout: FrameLayout? = null
    var fragment: Fragment? = null
    var fragmentManager1: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = binding.tabLayout
        frameLayout = binding.frameLayout

        fragment = TabFragmentDaftar()
        fragmentManager1 = childFragmentManager
        fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction!!.replace(R.id.frame_layout, fragment as TabFragmentDaftar)
        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction!!.commit()

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // creating cases for fragment
                when (tab.position) {
                    0 -> fragment = TabFragmentDaftar()
                    1 -> fragment = TabFragmentHasil()
                    2 -> fragment = TabFragmentRiwayat()

                }
                val fm = childFragmentManager
                val ft = fm.beginTransaction()
                ft.replace(R.id.frame_layout, fragment!!)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}