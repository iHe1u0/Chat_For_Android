package com.imorning.chat.activity.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(var route: String, var icon: ImageVector, var title: String) {
    object Message : NavigationItem("message", Icons.Filled.Message, "Message")
    object Contact : NavigationItem("contact", Icons.Filled.Contacts, "Contact")
    object Profile : NavigationItem("profile", Icons.Filled.Settings, "Profile")
}