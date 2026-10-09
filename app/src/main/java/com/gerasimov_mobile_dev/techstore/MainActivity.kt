package com.gerasimov_mobile_dev.techstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gerasimov_mobile_dev.techstore.ui.MainScreen
import com.gerasimov_mobile_dev.techstore.ui.theme.TechStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                TechStoreTheme {
                    val viewModel = TechStoreViewModel("https://gerasimov-sa.github.io/techstore-web/")
                    MainScreen(viewModel = viewModel)

            }

        }
    }
}

