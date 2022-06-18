package com.example.inventory.ui.fragment.admin

import android.content.ContentValues.TAG
import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.inventory.databinding.FragmentAdminBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvAdmin
        textView.text = "Admin"

        val mSpannableString = SpannableString(binding.tvGantiPassword.text)
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
        binding.tvGantiPassword.text = mSpannableString

        val mSpannableString1 = SpannableString(binding.tvLogout.text)
        mSpannableString1.setSpan(UnderlineSpan(), 0, mSpannableString1.length, 0)
        binding.tvLogout.text = mSpannableString1

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}