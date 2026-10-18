package com.gerasimov_mobile_dev.techstore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gerasimov_mobile_dev.techstore.ui.component.AnimatedSkeletonBox

@Composable
fun  SkeletonUiScreen() {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        columns = GridCells.Fixed(2),
    ){
        items(10){item ->
            AnimatedSkeletonBox(
                modifier = Modifier.fillMaxWidth()
                .height(130.dp)
                .padding(8.dp),
            cornerRadius = 12.dp
            )
            
        }
    }
}