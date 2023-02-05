package com.example.app3.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import  androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app3.data.entity.User



@Composable
fun LoginScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: RegistrationViewModel = viewModel()
    ){
    val viewState by viewModel.state.collectAsState()
    val username = remember{mutableStateOf("")}
    val password = remember{mutableStateOf("")}


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

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username.value,
            onValueChange = {text -> username.value = text},
            label = { Text(text = "Username")},
            shape = RoundedCornerShape(corner = CornerSize(50.dp))
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password.value,
            onValueChange = {passwordString -> password.value = passwordString},
            label = { Text(text = "Password")},
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(corner = CornerSize(50.dp))
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val (test, pass) = getusername(viewState.users, username.value)
                if(test != "no such user" && pass == password.value) {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            },
            shape = RoundedCornerShape(corner = CornerSize(50.dp)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            navController.navigate("register_screen") {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
                }
            },
            shape = RoundedCornerShape(corner = CornerSize(50.dp)),
            modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Register?")
            }
        }


}

private fun getusername(users: List<User>, userName: String): Pair<String, String> {
    try {
        val test = users.first { user -> user.username == userName }.username
        val test2 = users.first { user -> user.username == test }.password
        return Pair(test, test2)
    } catch (e: NoSuchElementException) {
        return Pair("no such user", "lol")
    }
}

