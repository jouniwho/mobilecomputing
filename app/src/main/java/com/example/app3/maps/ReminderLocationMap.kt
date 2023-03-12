package com.example.app3.maps

import android.graphics.Camera
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app3.MainActivity
import com.example.app3.ui.Reminder.ReminderViewModel
import com.example.app3.util.rememberMapViewLifeCycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.codemave.app3.util.viewModelProviderFactoryOf
import com.codemave.mobilecomputing.ui.home.categoryReminder.CategoryReminderViewModel
import com.example.app3.Graph
import com.example.app3.R
import com.example.app3.data.room.ReminderToCategory
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.PolylineOptions


@Composable
fun ReminderLocationMap(
    navController: NavController,
    //location: LocationDetails?,
){
    val applicationViewModel: appViewModel = viewModel<appViewModel>()
    val location = applicationViewModel.getLocationLiveData().observeAsState()
    val viewModel: CategoryReminderViewModel = viewModel(
        key = "category_list_2",
        factory = viewModelProviderFactoryOf { CategoryReminderViewModel(2) }
    )
    val viewState by viewModel.state.collectAsState()

    val list = viewState.payments

    val mapView = rememberMapViewLifeCycle()
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .padding(bottom = 36.dp)
    ){
        AndroidView({mapView}) { mapView ->
            coroutineScope.launch {
                val map = mapView.awaitMap()
                map.uiSettings.isZoomControlsEnabled = true
                val location2 = LatLng(65.006, 25.441)

                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(location2, 10f)
                )

                location.let {
                    val loc = LatLng(location.value!!.latitude.toDouble(), location.value!!.longitude.toDouble())
                    val markerOptions2 = MarkerOptions().title("Your location").position(loc)
                    map.addMarker(markerOptions2)
                }

                //val markerOptions = MarkerOptions()
                //    .title("Welcome to Oulu")
                //    .position(location2)
                //map.addMarker(markerOptions)

                setMapLongClick(map = map, navController = navController, list)

            }
        }
    }
}


private fun setMapLongClick(map: GoogleMap, navController: NavController, list: List<ReminderToCategory>){
    map.setOnMapClickListener {
        map.setOnMapLongClickListener { latlng ->
            val snippet = String.format(
                java.util.Locale.getDefault(),
                "Lat: %1$.2f, Lng: %2$.2f",
                latlng.latitude,
                latlng.longitude
            )

            map.addMarker (
                MarkerOptions().position(latlng).title("Reminder location").snippet(snippet)
            ).apply {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("location_data", latlng)
            }

            for (element in list){
                val x2 = element.remainder.locationX
                val y2 = element.remainder.locationY
                val x1 = latlng.latitude
                val y1 = latlng.longitude
                val distance = kotlin.math.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)))
                if (distance <= 0.00644){ // roughly 1000m
                    val location = LatLng(element.remainder.locationX, element.remainder.locationY)
                    val markerOptions = MarkerOptions()
                        .title("Reminder: ${element.remainder.message}")
                        .position(location)
                    map.addMarker(markerOptions)
                }
            }
        }

        }
    }

