package com.example.app3.ui.Reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import androidx.compose.ui.platform.LocalContext
import com.example.app3.Graph
import com.example.app3.home.categoryReminder.seen


class MyBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        val time = extras?.getString("time")
        val date = extras?.getString("date")
        val ID = extras?.getString("ID")
        val asd = intent.getStringExtra("time");
        //val identification = intent.getStringExtra("reminderID")!!.toLong()
        seen.time = time
        seen.date = date
        seen.remnindSeen = true
    }
}

