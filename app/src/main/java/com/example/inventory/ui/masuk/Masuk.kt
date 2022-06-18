package com.example.inventory.ui.masuk

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityMasukBinding
import com.example.inventory.ui.fragment.MainActivity
import com.example.inventory.util.TypeFaceUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Masuk : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMasukBinding
    lateinit var button: Button
    var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typefaceUtil = TypeFaceUtil()
        typefaceUtil.overridefonts(this, "SERIF", "font/interbold.ttf")

        val myFont: Typeface = Typeface.createFromAsset(this.assets, "font/interbold.ttf")
        binding.tvMasuk.typeface = myFont
        binding.etPassword.typeface = myFont
        binding.btnMasuk.typeface = myFont

        button = binding.btnMasuk
        button.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_masuk -> login()
        }
    }

    private fun login(){
        binding.tvSalah.visibility = View.INVISIBLE
        if (binding.etPassword.text.toString().isEmpty()) {
            binding.tvSalah.text = "Password tidak ada!"
            binding.tvSalah.visibility = View.VISIBLE
        } else if (binding.etPassword.text.toString().isNotEmpty()) {
            binding.pbLoading.visibility = View.VISIBLE
            val pass = db.collection("admin").document("admin")
            pass.get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val data = it.result.data
                        data?.let {
                            for ((_, value) in data) {
                                val password = value as String
                                if (binding.etPassword.text.toString() != password) {
                                    binding.tvSalah.text = "Password salah!"
                                    binding.tvSalah.visibility = View.VISIBLE
                                } else {
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                                binding.pbLoading.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
        }
    }
}