package com.example.app3.ui.Reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app3.data.entity.Reminder
import com.google.accompanist.insets.systemBarsPadding
import java.util.*

class remind{
    var mess = ""
    var locX = ""
    var locY = ""
    var remindTime = ""
    var remindDate = ""
    var remindCreation = ""
    var creatorId: Long = 0
    var remindSeen = false
    var remindCategory = ""
}

@Composable
fun UpdateReminder(
    reminder: Reminder,
    onClickSave: (remind) -> Unit,
    viewModel: ReminderViewModel = viewModel()
) {
    val updated = remind()

    val viewState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val title = rememberSaveable { mutableStateOf(reminder.message) }
    val category = rememberSaveable { mutableStateOf(reminder.reminderCategory) }
    val mCalendar = Calendar.getInstance()
    val loc_x = rememberSaveable { mutableStateOf(reminder.locationX) }
    val loc_y = rememberSaveable { mutableStateOf(reminder.locationY) }
    mCalendar.time = Date()
    val hour = mCalendar[Calendar.HOUR_OF_DAY]
    val minute = mCalendar[Calendar.MINUTE]
    val mContext = LocalContext.current
    val mYear = mCalendar.get(Calendar.YEAR)
    val mMonth = mCalendar.get(Calendar.MONTH)
    val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TopAppBar {
                IconButton(
                    onClick = {  }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
                Text(text = "Reminder")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title.value,
                    onValueChange = { title.value = it },
                    label = { Text(text = "Reminder title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                CategoryListDropdown(
                    viewState = viewState,
                    category = category
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (category.value == "Time") {

                    val mDate = remember { mutableStateOf(reminder.reminderDate) }

                    val mDatePickerDialog = DatePickerDialog(
                        mContext,
                        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                            mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
                        }, mYear, mMonth, mDay
                    )

                    var selectedTimeText by remember { mutableStateOf(reminder.reminderTime) }

                    val timePicker = TimePickerDialog(
                        mContext,
                        { _, selectedHour: Int, selectedMinute: Int ->
                            selectedTimeText = "$selectedHour:$selectedMinute"
                        }, hour, minute, false
                    )

                    Text(
                        text = if (selectedTimeText.isNotEmpty()) {
                            "Selected time is $selectedTimeText"
                        } else {
                            "Please select the time"
                        }
                    )

                    Button(
                        onClick = {
                            timePicker.show()
                        }
                    ) {
                        Text(text = "Select time")
                    }

                    Button(onClick = {
                        mDatePickerDialog.show()
                    }) {
                        Text(
                            text = "Open Date Picker",
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "Selected Date: ${mDate.value}",
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    )

                    updated.mess = title.value
                    updated.remindDate = mDate.value
                    updated.remindTime = selectedTimeText
                    updated.remindCategory = category.value

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        enabled = true,
                        onClick = {
                            onClickSave(updated)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(55.dp)
                    ) {
                        Text("Save notification")
                    }
                }
            }

            if (category.value == "Location") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = loc_x.value,
                    onValueChange = { loc_x.value = it },
                    label = { Text(text = "location X") },
                )

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = loc_y.value,
                    onValueChange = { loc_y.value = it },
                    label = { Text(text = "location Y") },
                )

                updated.mess = title.value
                updated.locX = loc_x.value
                updated.locY = loc_y.value
                updated.remindCategory = category.value


                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    enabled = true,
                    onClick = {

                        onClickSave(updated)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(55.dp)
                ) {
                    Text("Save notification")
                }
            }

        }
    }
}
