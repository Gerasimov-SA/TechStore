package com.gerasimov_mobile_dev.techstore.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SplashScreen(){
    Text(modifier = Modifier.fillMaxSize(),
        text = "Это SplashScreen")
}