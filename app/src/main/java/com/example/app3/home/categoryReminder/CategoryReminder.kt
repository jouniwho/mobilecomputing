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
import com.example.app3.ui.Reminder.ReminderViewModel
import com.example.app3.ui.Reminder.UpdateReminder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CategoryReminder(
    categoryId: Long,
    modifier: Modifier = Modifier
) {
    val viewModel: CategoryReminderViewModel = viewModel(
        key = "category_list_$categoryId",
        factory = viewModelProviderFactoryOf { CategoryReminderViewModel(categoryId) }
    )
    val viewState by viewModel.state.collectAsState()

    Column(modifier = modifier) {
        ReminderList(
            list = viewState.payments,
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
    list: List<ReminderToCategory>
) {
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(list) { item ->
            ReminderListItem(
                list = list,
                reminder = item.remainder,
                category = item.category,
                onClick = {},
                modifier = Modifier.fillParentMaxWidth(),
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
    list: List<ReminderToCategory>,
    reminder: Reminder,
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReminderViewModel = viewModel(),
) {
    val viewState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val reminderIdState: MutableState<Long?> = rememberSaveable { mutableStateOf(null) }
    val popupState = rememberSaveable { mutableStateOf(PopupState.Close) }

    if(seen.remnindSeen){
        val teest = getreminderwithtime(list, seen.time, seen.date)
        coroutineScope.launch {viewModel.updateReminder(
            Reminder(
                id = teest +1,
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
        seen.remnindSeen = false
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

private fun Date.formatToString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(this)
}

fun Long.toDateString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(this))

}