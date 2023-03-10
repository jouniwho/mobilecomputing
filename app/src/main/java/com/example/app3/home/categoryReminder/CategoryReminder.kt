package com.example.app3.home.categoryReminder


import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codemave.app3.util.viewModelProviderFactoryOf
import com.codemave.mobilecomputing.ui.home.categoryReminder.CategoryReminderViewModel

import com.example.app3.R
import com.example.app3.data.entity.Category
import com.example.app3.data.entity.Reminder
import com.example.app3.data.room.ReminderToCategory
import com.example.app3.maps.LocationDetails
import com.example.app3.ui.Reminder.ReminderViewModel
import com.example.app3.ui.Reminder.UpdateReminder
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.lang.Math.pow
import java.lang.Math.sqrt
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.math.pow

@Composable
fun CategoryReminder(
    categoryId: Long,
    modifier: Modifier = Modifier,
    location: LocationDetails?
) {
    val viewModel: CategoryReminderViewModel = viewModel(
        key = "category_list_$categoryId",
        factory = viewModelProviderFactoryOf { CategoryReminderViewModel(categoryId) }
    )
    val viewState by viewModel.state.collectAsState()

    Column(modifier = modifier) {
        ReminderList(
            list = viewState.payments,
            location = location
        )
        //Button(
        //    onClick = {},
        //    shape = RoundedCornerShape(corner = CornerSize(50.dp)),
        //
        //    ) {
        //    Text(text = "TEST")
        //}
    }
}

@Composable
private fun ReminderList(
    list: List<ReminderToCategory>,
    location: LocationDetails?
) {
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(list) { item ->
            ReminderListItem(
                reminder = item.remainder,
                category = item.category,
                onClick = {},
                modifier = Modifier.fillParentMaxWidth(),
                location = location
            )

        }
    }
}

private enum class PopupState{
    Open, Close
}

object seen{
    var remnindSeen = false
    var time: String? = ""
    var date: String? = ""
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun ReminderListItem(
    reminder: Reminder,
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReminderViewModel = viewModel(),
    location: LocationDetails?
) {
    val viewState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val reminderIdState: MutableState<Long?> = rememberSaveable { mutableStateOf(null) }
    val popupState = rememberSaveable { mutableStateOf(PopupState.Close) }
    if(reminder.reminderTime != ""){
        if(countTime(reminder.reminderTime, reminder.reminderDate) <= 0){
            coroutineScope.launch {viewModel.updateReminder(
                Reminder(
                    id = reminder.id,
                    reminderCategoryId = reminder.reminderCategoryId,
                    reminderSeen = true,
                    message = reminder.message,
                    reminderDate = reminder.reminderDate,
                    reminderTime = reminder.reminderTime,
                    locationX = reminder.locationX,
                    locationY = reminder.locationY,
                    reminderCategory = reminder.reminderCategory
                )
            )}
        }
    }
    /*if(reminder.reminderTime == "" && reminder.locationX == 0.0 && reminder.locationY == 0.0){
        coroutineScope.launch {viewModel.updateReminder(
            Reminder(
                id = reminder.id,
                reminderCategoryId = reminder.reminderCategoryId,
                reminderSeen = true,
                message = reminder.message,
                reminderDate = reminder.reminderDate,
                reminderTime = reminder.reminderTime,
                locationX = reminder.locationX,
                locationY = reminder.locationY,
                reminderCategory = reminder.reminderCategory
            )
        )}
    }*/

    location?.let {
        val x1 = location.latitude.toDouble()
        val x2 = reminder.locationX
        val y1 = location.longitude.toDouble()
        val y2 = reminder.locationY
        val distance = kotlin.math.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)))

        if (distance <= 0.00046 && !reminder.reminderSeen){ //this is roughly 50m
            coroutineScope.launch {viewModel.updateReminder(
                Reminder(
                    id = reminder.id,
                    reminderCategoryId = reminder.reminderCategoryId,
                    reminderSeen = true,
                    message = reminder.message,
                    reminderDate = reminder.reminderDate,
                    reminderTime = reminder.reminderTime,
                    locationX = reminder.locationX,
                    locationY = reminder.locationY,
                    reminderCategory = reminder.reminderCategory,
                    notification = true
                )
            )}
        }
    }

    if(reminder.reminderSeen) {
        ConstraintLayout(modifier = modifier.clickable { onClick() }.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    coroutineScope.launch { viewModel.deleteReminder(reminder) }
                }
            )
        }) {

            val (divider, paymentTitle, paymentCategory, icon, date) = createRefs()

            Divider(
                Modifier.constrainAs(divider) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                    width = Dimension.fillToConstraints
                }
            )

            // title
            Text(
                text = reminder.message,
                maxLines = 1,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.constrainAs(paymentTitle) {
                    linkTo(
                        start = parent.start,
                        end = icon.start,
                        startMargin = 24.dp,
                        endMargin = 16.dp,
                        bias = 0f // float this towards the start. this was is the fix we needed
                    )
                    top.linkTo(parent.top, margin = 10.dp)
                    width = Dimension.preferredWrapContent
                }
            )

            // category
            Text(
                text = category.name,
                maxLines = 1,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.constrainAs(paymentCategory) {
                    linkTo(
                        start = parent.start,
                        end = icon.start,
                        startMargin = 24.dp,
                        endMargin = 8.dp,
                        bias = 0f // float this towards the start. this was is the fix we needed
                    )
                    top.linkTo(paymentTitle.bottom, margin = 6.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    width = Dimension.preferredWrapContent
                }
            )

            var textToShow = ""

            if (category.name == "Time") {
                textToShow = "${reminder.reminderTime} ${reminder.reminderDate}"
            } else {
                textToShow = "(${reminder.locationX} ${reminder.locationY})"
            }
            // date or location
            Text(
                text = textToShow,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.constrainAs(date) {
                    linkTo(
                        start = paymentCategory.end,
                        end = icon.start,
                        startMargin = 8.dp,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    centerVerticallyTo(paymentCategory)
                    top.linkTo(paymentTitle.bottom, 6.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                }
            )


            // icon
            IconButton(
                onClick = {
                    popupState.value = PopupState.Open
                    reminderIdState.value = reminder.id
                },
                modifier = Modifier
                    .size(50.dp)
                    .padding(6.dp)
                    .constrainAs(icon) {
                        top.linkTo(parent.top, 10.dp)
                        bottom.linkTo(parent.bottom, 10.dp)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit_square)
                )
            }

            when (popupState.value) {
                PopupState.Open -> {
                    UpdateReminder(
                        reminder = reminder,
                        onClickSave = {
                            coroutineScope.launch {
                                viewModel.updateReminder(
                                    Reminder(
                                        id = reminder.id,
                                        reminderCategoryId = getCategoryId(
                                            viewState.categories,
                                            it.remindCategory
                                        ),
                                        message = it.mess,
                                        reminderDate = it.remindDate,
                                        reminderTime = it.remindTime,
                                        locationX = it.locX,
                                        locationY = it.locY,
                                        reminderCategory = it.remindCategory,
                                    )
                                )
                            }
                            popupState.value = PopupState.Close
                        }
                    )
                }
                PopupState.Close -> {}
            }

            PickImageFromGallery()

        }

    }

}

private fun getCategoryId(categories: List<Category>, categoryName: String): Long {
    return categories.first { category -> category.name == categoryName }.id
}

private fun getreminderwithtime(reminders: List<ReminderToCategory>, time: String?, date: String?): Long {
    reminders.forEach{
        item ->
        val testi = item.remainder.reminderDate
        val toine = item.remainder.reminderTime
        if(testi == date && toine == time){
            return item.remainder.id
        }
    }
    return 0
}

private fun check_distance(location_reminderX: Double, location_reminderY: Double, location_user: LocationDetails?){

}

private fun countTime(remindertime: String, reminderdate: String): Long {
    // time = "$mDay $mMonth $mYear $hour $minute"
    //val date = time.split(" ")
    val test = LocalDate.now().toString()
    val dtf3 = SimpleDateFormat("dd-MM-yyyy")
    val dtf2 = SimpleDateFormat("yyyy-MM-dd")
    val test3 = dtf2.parse(test)
    val test4 = dtf3.format(test3)
    val new_date = test4.replace("-", "/")
    val test2 = LocalTime.now().toString()
    val sdf1 = SimpleDateFormat("HH:mm:ss")
    val sdf2 = SimpleDateFormat("HH:mm")
    val dateee = sdf1.parse(test2)
    val dte = sdf2.format(dateee!!)
    val current_time = "$new_date $dte"
    //val dateone = "${date[0]}/${date[1]}/${date[2]} ${date[3]}:${date[4]}"
    val datetwo = "$reminderdate $remindertime"
    val mDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
    val mDate2 = mDateFormat.parse(datetwo)
    val date2 = mDateFormat.format(mDate2)
    val mDate1 = mDateFormat.parse(current_time)
    val mDate3 = mDateFormat.parse(date2)

    // Finding the absolute difference between
    // the two dates.time (in milli seconds)
    val mDifference = (mDate3.time - mDate1.time)
    val dif = mDifference
    // Converting milli seconds to minutes
    return mDifference / (60 * 1000)

}

private fun Date.formatToString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(this)
}

fun Long.toDateString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(this))

}