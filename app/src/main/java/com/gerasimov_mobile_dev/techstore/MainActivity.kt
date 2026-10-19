package com.gerasimov_mobile_dev.techstore

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.runtime.rememberCoroutineScope

import androidx.navigation.compose.rememberNavController
import com.gerasimov_mobile_dev.techstore.navigation.TechStoreNavGraph

import com.gerasimov_mobile_dev.techstore.ui.theme.TechStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechStoreTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                TechStoreNavGraph(
                    navController = navController,
                    viewModelFactory = {
                        TechStoreViewModel()
                    },
                    scope = scope
                )
            }
        }
    }
}

