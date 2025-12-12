package com.taqsiim.compusconnect.ui.navigation

import androidx.compose.runtime.Composable
import com.taqsiim.compusconnect.features.clubManager.ManagerProfileScreen
import com.taqsiim.compusconnect.features.student.StudentProfileScreen

@Composable
fun StudentAppRoot(onSwitchRole: () -> Unit) {
    // In a real app, this would be a NavHost. For now, we just show the ProfileScreen.
    StudentProfileScreen(onSwitchRole = onSwitchRole)
}

@Composable
fun ManagerAppRoot(onSwitchRole: () -> Unit) {
    // In a real app, this would be a NavHost. For now, we just show the ProfileScreen.
    ManagerProfileScreen(onSwitchRole = onSwitchRole)
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun StudentAppRootPreview() {
    com.taqsiim.compusconnect.ui.theme.CampusAppTheme(userRole = com.taqsiim.compusconnect.ui.theme.UserRole.Student) {
        StudentAppRoot(onSwitchRole = {})
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ManagerAppRootPreview() {
    com.taqsiim.compusconnect.ui.theme.CampusAppTheme(userRole = com.taqsiim.compusconnect.ui.theme.UserRole.ClubManager) {
        ManagerAppRoot(onSwitchRole = {})
    }
}
