package com.example.inventory.ui.masuk

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityMasukBinding
import com.example.inventory.ui.fragment.MainActivity
import com.example.inventory.util.TypeFaceUtil
import com.google.firebase.firestore.FirebaseFirestore

class Masuk : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMasukBinding
    private lateinit var db: FirebaseFirestore
    private var i = 0
    private var userpass = arrayOf("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typefaceUtil = TypeFaceUtil()
        typefaceUtil.overridefonts(this, "SERIF", "font/interbold.ttf")

        val myFont: Typeface = Typeface.createFromAsset(this.assets, "font/interbold.ttf")
        binding.tvMasuk.typeface = myFont
        binding.etUsername.typeface = myFont
        binding.etPassword.typeface = myFont
        binding.btnMasuk.typeface = myFont
        binding.tvSalah.typeface = myFont

        db = FirebaseFirestore.getInstance()

        binding.btnMasuk.setOnClickListener(this)

        binding.etPassword.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                login()
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_masuk -> login()
        }
    }

    private fun login() {
        binding.tvSalah.visibility = View.INVISIBLE
        when {
            binding.etPassword.text.toString().isEmpty() -> {
                binding.tvSalah.text = "Password tidak ada!"
                binding.tvSalah.visibility = View.VISIBLE
            }
            binding.etUsername.text.toString().isEmpty() -> {
                binding.tvSalah.text = "Username tidak ada!"
                binding.tvSalah.visibility = View.VISIBLE
            }
            binding.etPassword.text.toString().isNotEmpty() -> {
                binding.pbLoading.visibility = View.VISIBLE
                val pass = db.collection("admin").document("admin")
                pass.get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val data = it.result.data
                            data?.let {
                                for ((_, value) in data) {
                                    val password = value as String
                                    userpass[i] = password
                                    i++
                                }
                                i = 0
                                if (binding.etUsername.text.toString() == userpass[1] && binding.etPassword.text.toString() == userpass[0]) {
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                } else {
                                    binding.tvSalah.text = "Username / Password salah!"
                                    binding.tvSalah.visibility = View.VISIBLE
                                }
                                binding.pbLoading.visibility = View.INVISIBLE
                            }
                        }
                    }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}