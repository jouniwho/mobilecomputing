package com.example.app3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app3.home.Home
import com.example.app3.ui.Reminder.Reminder
import com.example.app3.ui.login.LoginScreen
import com.example.app3.ui.login.RegistrationScreen
import com.example.app3.ui.profile.UIprofile
import com.example.app3.ui.theme.App3Theme

class MainActivity() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App3Theme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //Greeting("Android")
                    //LoginScreen(modifier = Modifier.fillMaxSize())
                    LoginAndRegistration()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    App3Theme {
        Greeting("Android")
    }
}


@Composable
fun LoginAndRegistration(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login_screen", builder = {
        composable("reminder") { Reminder(navController = navController)}
        composable("login_screen", content = { LoginScreen(navController = navController, modifier = Modifier.fillMaxSize()) })
        composable("register_screen", content = { RegistrationScreen(navController = navController) })
        composable("home", content = { Home(navController = navController) })
        composable("profile", content = { UIprofile(navController = navController)})
    })
}


