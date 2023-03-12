package com.example.app3.ui.Reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.widget.DatePicker
import android.widget.Toast
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app3.Graph
import com.example.app3.data.entity.Category
import com.example.app3.data.entity.User
import com.example.app3.maps.LocationDetails
import com.example.app3.maps.appViewModel
import com.google.accompanist.insets.systemBarsPadding
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@Composable
fun Reminder(
    navController: NavController,
    viewModel: ReminderViewModel = viewModel(),
    //location: LocationDetails?
) {
    val coroutineScope = rememberCoroutineScope()
    val viewState by viewModel.state.collectAsState()
    val context = LocalContext.current
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
    var selectedTimeText by remember { mutableStateOf("") }
    val mDate = remember { mutableStateOf("") }
    val applicationViewModel: appViewModel = viewModel<appViewModel>()
    val location = applicationViewModel.getLocationLiveData().observeAsState()
    val latlng = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<LatLng>("location_data")
        ?.value

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

                    val mDatePickerDialog = DatePickerDialog(
                        mContext,
                        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                            mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
                        }, mYear, mMonth, mDay
                    )

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
                        if(loc_x.value == "" && loc_y.value == "" && selectedTimeText != "" &&  mDate.value != ""){
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
                                        locationX = 0.0,
                                        locationY = 0.0,
                                        reminderCreation = "$mDay ${mMonth+1} $mYear $hour $minute",
                                        notification = checkedState.value
                                    )
                                )
                            }
                            navController.navigate("home") {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }else {
                            Toast.makeText(
                                context,
                                "Date and Time required",
                                Toast.LENGTH_SHORT
                            ).show()
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
                if (latlng == null) {
                    OutlinedButton(
                        onClick = { navController.navigate("map")
                        },
                        modifier = Modifier.height(55.dp)
                    ) {
                        Text(text = "Reminder location")
                    }
                } else {
                    //Text(
                    //    text = "Lat: ${latlng.latitude}, \nLng: ${latlng.longitude}"
                    //)
                    loc_x.value = latlng.latitude.toString()
                    loc_y.value = latlng.longitude.toString()
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
                }

                GPS(location.value)


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
                        if(loc_x.value != "" && loc_y.value != "" && selectedTimeText == "" ){
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
                                    locationX = loc_x.value.toDouble(),
                                    locationY = loc_y.value.toDouble(),
                                    reminderCategory = category.value,
                                    notification = checkedState.value
                                )
                            )
                        }
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        }else{
                            Toast.makeText(
                                context,
                                "Location required",
                                Toast.LENGTH_SHORT
                            ).show()
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

@Composable
private fun GPS(location: LocationDetails?){
    location?.let{
        Text(text="Your location ${location.latitude}, ${location.longitude}")
    }
}

