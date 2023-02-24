package com.example.app3.ui.Reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app3.data.entity.Category
import com.example.app3.data.entity.User
import com.google.accompanist.insets.systemBarsPadding
import java.util.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@Composable
fun Reminder(
    navController: NavController,
    viewModel: ReminderViewModel = viewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val viewState by viewModel.state.collectAsState()

    val title = rememberSaveable { mutableStateOf("") }
    val category = rememberSaveable { mutableStateOf("") }
    val loc_x = rememberSaveable { mutableStateOf("") }
    val loc_y = rememberSaveable { mutableStateOf("") }
    val mCalendar = Calendar.getInstance()
    mCalendar.time = Date()
    val hour = mCalendar[Calendar.HOUR_OF_DAY]
    val minute = mCalendar[Calendar.MINUTE]
    val mContext = LocalContext.current
    val mYear = mCalendar.get(Calendar.YEAR)
    val mMonth = mCalendar.get(Calendar.MONTH)
    val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
    val checkedState = remember { mutableStateOf(true) }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TopAppBar {
                IconButton(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
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

                    val mDate = remember { mutableStateOf("") }

                    val mDatePickerDialog = DatePickerDialog(
                        mContext,
                        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                            mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
                        }, mYear, mMonth, mDay
                    )

                    var selectedTimeText by remember { mutableStateOf("") }
                    val timeformat = SimpleDateFormat("HH:mm")

                    val timePicker = TimePickerDialog(
                        mContext,
                        { _, selectedHour: Int, selectedMinute: Int ->
                            val parsed = timeformat.parse("$selectedHour:$selectedMinute")
                            val formattedtime = timeformat.format(parsed!!)
                            selectedTimeText = formattedtime
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
                            text = "Open Date Picker"
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "Selected Date: ${mDate.value}",
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    )

                Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        Checkbox(
                            checked = checkedState.value,
                            onCheckedChange = { checkedState.value = it },
                        )
                        Text(text = "Make a notification?")
                    }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    enabled = true,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.saveReminder(
                                com.example.app3.data.entity.Reminder(
                                    message = title.value,
                                    reminderCategoryId = getCategoryId(
                                        viewState.categories,
                                        category.value
                                    ),
                                    reminderDate = mDate.value,
                                    reminderTime = selectedTimeText,
                                    reminderCategory = category.value,
                                    locationX = 0.0f,
                                    locationY = 0.0f,
                                    reminderCreation = "$mDay ${mMonth+1} $mYear $hour $minute",
                                    notification = checkedState.value
                                )
                            )
                        }
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(55.dp)
                ) {
                    Text("Save notification")
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
                Row {
                    Checkbox(
                        checked = checkedState.value,
                        onCheckedChange = { checkedState.value = it },
                    )
                    Text(text = "Make a notification?")
                }


                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    enabled = true,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.saveReminder(
                                com.example.app3.data.entity.Reminder(
                                    message = title.value,
                                    reminderCategoryId = getCategoryId(
                                        viewState.categories,
                                        category.value
                                    ),
                                    reminderDate = "",
                                    reminderTime = "",
                                    locationX = loc_x.value.toFloat(),
                                    locationY = loc_y.value.toFloat(),
                                    reminderCategory = category.value,
                                    notification = checkedState.value
                                )
                            )
                        }
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
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
}

private fun getCategoryId(categories: List<Category>, categoryName: String): Long {
    return categories.first { category -> category.name == categoryName }.id
}

private fun getuserId(users: List<User>, userName: String): Long {
    return try {
        val test = users.first { user -> user.username == userName }.id
        test
    }
    catch(e: NoSuchElementException){
        404
    }
}

@Composable
fun CategoryListDropdown(
    viewState: ReminderViewState,
    category: MutableState<String>
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) {
        Icons.Filled.ArrowDropUp // requires androidx.compose.material:material-icons-extended dependency
    } else {
        Icons.Filled.ArrowDropDown
    }

    Column {
        OutlinedTextField(
            value = category.value,
            onValueChange = { category.value = it},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Category") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            viewState.categories.forEach { dropDownOption ->
                DropdownMenuItem(
                    onClick = {
                        category.value = dropDownOption.name
                        expanded = false
                    }
                ) {
                    Text(text = dropDownOption.name)
                }

            }
        }
    }
}

