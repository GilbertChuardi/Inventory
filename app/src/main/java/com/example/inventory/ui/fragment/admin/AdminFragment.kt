package com.example.inventory.ui.fragment.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.inventory.R
import com.example.inventory.databinding.FragmentAdminBinding
import com.example.inventory.ui.masuk.Masuk
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val textView: TextView = binding.tvAdmin
        textView.text = "Admin"

        val mSpannableString = SpannableString(binding.tvGantiPassword.text)
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
        binding.tvGantiPassword.text = mSpannableString

        val mSpannableString1 = SpannableString(binding.tvLogout.text)
        mSpannableString1.setSpan(UnderlineSpan(), 0, mSpannableString1.length, 0)
        binding.tvLogout.text = mSpannableString1

        binding.tvGantiPassword.setOnClickListener(this)
        binding.tvLogout.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_ganti_password -> gantiPass()
            R.id.tv_logout -> logout()
        }
    }

    private fun gantiPass() {
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alertdialog_ganti_password, null)
        val editText1 = dialogLayout.findViewById<EditText>(R.id.et_ganti_password)
        val builder = AlertDialog.Builder(context)
            .setTitle("Masukkan assword baru")
            .setView(dialogLayout)
            .setPositiveButton("OK", null)
            .show()

        val mPositiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            if (editText1.text.toString().isNotEmpty()) {
                db.collection("admin").document("admin")
                    .update("password", editText1.text.toString())
                    .addOnSuccessListener {
                        Toast.makeText(context, "Password sudah diganti", Toast.LENGTH_SHORT).show()
                    }

                builder.dismiss()
            } else {
                Toast.makeText(context, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        startActivity(Intent(context, Masuk::class.java))
    }

}