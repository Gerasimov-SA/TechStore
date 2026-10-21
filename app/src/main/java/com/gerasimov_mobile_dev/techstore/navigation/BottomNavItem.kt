package com.gerasimov_mobile_dev.techstore.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Главная", Icons.Filled.Home)
    object Search : BottomNavItem("search", "Поиск", Icons.Filled.Search)
    object Cart : BottomNavItem("cart", "Корзина", Icons.Filled.ShoppingCart)
    object Profile : BottomNavItem("profile", "Профиль", Icons.Filled.Person)
}

val allBottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Search,
    BottomNavItem.Cart,
    BottomNavItem.Profile
)