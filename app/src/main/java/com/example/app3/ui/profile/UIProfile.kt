package com.example.app3.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun UIProfile(
    navController: NavController,
    modifier: Modifier,
){

    Column(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ){

        Icon(
            painter = rememberVectorPainter(Icons.Filled.Person),
            contentDescription = "login_image",
            modifier = Modifier
                .fillMaxWidth()
                .size(150.dp),
        )
        
        Text(text = "Cool profile page",
        textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            shape = RoundedCornerShape(corner = CornerSize(50.dp)),

            ) {
            Text(text = "Back")
        }

    }


}