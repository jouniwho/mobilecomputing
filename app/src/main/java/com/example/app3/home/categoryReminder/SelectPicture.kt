package com.example.app3.home.categoryReminder

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun PickImageFromGallery(){
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        selectedImage = uri
    }

    ShowImage(selectedImage) {
        launcher.launch("image/*")
    }
}

@Composable
fun ShowImage(
    selectedImage: Uri? = null,
    onImageClick: () -> Unit
    ) {

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(1.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (selectedImage != null)
                    Image(
                        painter = rememberAsyncImagePainter(selectedImage),
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .clip(CircleShape)
                            .fillMaxSize()
                            .clickable {
                                onImageClick()
                            })
                else
                    OutlinedButton(onClick = onImageClick) {
                        Text(text = "Choose Image")
                    }
            }
        }
