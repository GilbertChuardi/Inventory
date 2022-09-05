package com.example.inventory.ui.masuk

import android.app.AlarmManager.RTC_WAKEUP
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R
import com.example.inventory.databinding.ActivityMasukBinding
import com.example.inventory.ui.fragment.MainActivity
import com.example.inventory.util.AlarmManager
import com.example.inventory.util.TypeFaceUtil
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

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
        //setAlarm1()
        //createNotificationChannel()
    }

    /*private fun setAlarm1() {
        var calender: Calendar
        calender = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY, 9)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)
        val alarmManager:AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val thuReq: Long = Calendar.getInstance().timeInMillis + 1
        var reqReqCode = thuReq.toInt()
        if (calender.timeInMillis < System.currentTimeMillis()) {
            calender.add(Calendar.DAY_OF_YEAR, 1)
        }
        val alarmTimeMilsec = calender.timeInMillis
        val intent = Intent(this, AlarmManager::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        val pendingIntent = PendingIntent.getBroadcast(this, reqReqCode, intent, 0)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calender.timeInMillis,
            9 * 60 * 60 * 1000,
            pendingIntent
        )
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarmclock Channel"
            val description = " Reminder Alarm manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNELID, name, importance)
            notificationChannel.description = description
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }*/

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_masuk -> login()
        }
    }

    private fun login() {
        binding.tvSalah.visibility = View.INVISIBLE
        when {
            binding.etUsername.text.toString().isEmpty() -> {
                binding.tvSalah.text = "Username tidak ada!"
                binding.tvSalah.visibility = View.VISIBLE
            }

            binding.etPassword.text.toString().isEmpty() -> {
                binding.tvSalah.text = "Password tidak ada!"
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