package com.example.inventory.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.inventory.R
import com.example.inventory.ui.fragment.MainActivity
import java.util.*

class AlarmManager: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent1 = Intent(context, MainActivity::class.java)
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent1,PendingIntent.FLAG_IMMUTABLE)

                val builder = NotificationCompat.Builder(context, "111")
                    .setSmallIcon(R.drawable.ic_laporan)
                    .setContentTitle("Alarm is running")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(R.drawable.ic_hapus,"Stop",pendingIntent)
                    .setContentIntent(pendingIntent)

                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val r = RingtoneManager.getRingtone(context, notification)
                r.play()

                val notificationManagerCompat = NotificationManagerCompat.from(context)
                notificationManagerCompat.notify(123, builder.build())

            }
        }
    }
}