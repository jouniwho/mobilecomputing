package com.example.app3.ui.Reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat.from
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.app3.Graph
import com.example.app3.R
import com.example.app3.data.entity.Category
import com.example.app3.data.entity.Reminder
import com.example.app3.data.entity.User
import com.example.app3.data.repository.CategoryRepository
import com.example.app3.data.repository.ReminderRepository
import com.example.app3.data.room.ReminderToCategory
import com.example.app3.util.NotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
        //setOneTimeNotification() I'll work on notifications later
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

private fun createReminderMadeNotification(reminder: Reminder){
    val notificationId = 2
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("New reminder made")
        .setContentText("You set reminder")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
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
            return
        }
        notify(notificationId, builder.build())
    }
}
