package com.gerasimov_mobile_dev.techstore.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector,val url:String) {
    object Home : BottomNavItem("home", "Главная", Icons.Filled.Home,"https://hop.uz/")
    object Search : BottomNavItem("search", "Поиск", Icons.Filled.Search,"https://hop.uz/ad/create/")
    object Cart : BottomNavItem("cart", "Корзина", Icons.Filled.ShoppingCart,"https://hop.uz/profile/favorites/")
    object Profile : BottomNavItem("profile", "Профиль", Icons.Filled.Person,"https://hop.uz/profile/")
}

val allBottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Search,
    BottomNavItem.Cart,
    BottomNavItem.Profile
)