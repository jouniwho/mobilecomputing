package com.example.app3.ui.Reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat.from
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.app3.Graph
import com.example.app3.MainActivity
import com.example.app3.R
import com.example.app3.data.entity.Category
import com.example.app3.data.entity.Reminder
import com.example.app3.data.entity.User
import com.example.app3.data.repository.CategoryRepository
import com.example.app3.data.repository.ReminderRepository
import com.example.app3.util.NotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class ReminderViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository,
    private val categoryRepository: CategoryRepository = Graph.categoryRepository
): ViewModel() {
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    suspend fun saveReminder(reminder: Reminder): Long {
        createReminderMadeNotification(reminder)
        return reminderRepository.addReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        return reminderRepository.editReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        return reminderRepository.deleteReminder(reminder)
    }


    init {
        createNotificationChannel(context = Graph.appContext)
        //setOneTimeNotification()
        viewModelScope.launch {
            categoryRepository.categories().collect { reminders ->
                _state.value = ReminderViewState(reminders)
            }
        }
    }


}

data class ReminderViewState(
    val categories: List<Category> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val selectedReminder: Reminder? = null,
    val users: List<User> = emptyList()
)

private fun setOneTimeNotification(){
    val workManager = WorkManager.getInstance(Graph.appContext)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(10, TimeUnit.SECONDS)
        .setConstraints(constraints)
        .build()

    workManager.enqueue(notificationWorker)

    //Monitoring for state of work
    workManager.getWorkInfoByIdLiveData(notificationWorker.id)
        .observeForever{ workInfo ->
            if (workInfo.state == WorkInfo.State.SUCCEEDED){
                createSuccessNotification()
            }
//          else{
//                createErrorNotification()
//           }

        }
}

private fun createNotificationChannel(context: Context){
    //API 26+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val name = "NotificationChannelName"
        val descriptionText = "NotificationChannelText"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply{
            description = descriptionText
        }
        //register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}

private fun createSuccessNotification(){
    val notificationId = 1
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Success! Download complete")
        .setContentText("Your countdown completed successfully")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build())
    }
}

private fun createErrorNotification(){
    val notificationId = 3
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Something went wrong")
        .setContentText("tou")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build())
    }

}

fun createReminderMadeNotification(reminder: Reminder){
    val notificationId = 2
    val date = reminder.reminderDate
    val timee = reminder.reminderTime
    val idd = reminder.id

    val activityIntent = Intent(Graph.appContext, MainActivity::class.java)
    val activityPendingIntent = PendingIntent.getActivity(
        Graph.appContext,
        1,
        activityIntent,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_MUTABLE else 0
    )


    val broadcast = Intent(Graph.appContext, MyBroadcastReceiver::class.java)
    broadcast.putExtra("date", date)
    broadcast.putExtra("time", timee)
    broadcast.putExtra("ID", idd)

    val incrementIntent = PendingIntent.getBroadcast(
        Graph.appContext,
        2,
        broadcast,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_MUTABLE else 0,

    )

    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle(reminder.message)
        .setContentText("@${reminder.reminderTime} ${reminder.reminderDate}")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(activityPendingIntent)
        .addAction(
            R.drawable.ic_launcher_background,
            "asd",
            incrementIntent,
        )


    val workManager = WorkManager.getInstance(Graph.appContext)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    //count time to the notification in minutes
    val time = countTime(reminder.reminderCreation, reminder.reminderTime, reminder.reminderDate)

    val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(time, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    workManager.enqueue(notificationWorker)

    //Monitoring for state of work
    workManager.getWorkInfoByIdLiveData(notificationWorker.id)
        .observeForever{ workInfo ->
            if (workInfo.state == WorkInfo.State.SUCCEEDED){
                with(from(Graph.appContext)) {
                    if (ActivityCompat.checkSelfPermission(
                            Graph.appContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@observeForever
                    }
                    notify(notificationId, builder.build())
                    }
            }
        }
}

private fun countTime(time: String, originalTime: String, originaldate: String): Long {
    // time = "$mDay $mMonth $mYear $hour $minute"
    val date = time.split(" ")

    val dateone = "${date[0]}/${date[1]}/${date[2]} ${date[3]}:${date[4]}"
    val datetwo = "$originaldate $originalTime"
    val mDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")

    val mDate1 = mDateFormat.parse(dateone)
    val mDate2 = mDateFormat.parse(datetwo)

    // Finding the absolute difference between
    // the two dates.time (in milli seconds)
    val mDifference = kotlin.math.abs(mDate1.time - mDate2.time)

    // Converting milli seconds to minutes
    return mDifference / (60 * 1000)

}

