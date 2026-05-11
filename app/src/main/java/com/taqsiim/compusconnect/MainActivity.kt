package com.taqsiim.compusconnect

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.ui.auth.LoginScreen
import com.taqsiim.compusconnect.ui.navigation.ManagerAppRoot
import com.taqsiim.compusconnect.ui.navigation.StudentAppRoot
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import com.taqsiim.compusconnect.ui.auth.AuthViewModel
import com.taqsiim.compusconnect.ui.auth.AuthIntent
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusAppTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var currentUserRole by remember { mutableStateOf<UserRole?>(null) }
    val authState by authViewModel.state.collectAsState()
    val currentUser = authState.currentUser

    LaunchedEffect(currentUser) {
        Log.d(TAG, "CurrentUser changed: ${currentUser?.email}, role: ${currentUser?.role}")
        currentUserRole = currentUser?.role
        Log.d(TAG, "CurrentUserRole updated to: $currentUserRole")
    }
    
    val handleLogout: () -> Unit = {
        Log.d(TAG, "Logging out...")
        authViewModel.processIntent(AuthIntent.Logout)
        currentUserRole = null
        Log.d(TAG, "Logout complete, currentUserRole set to null")
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentUserRole) {
                null -> {
                    Log.d(TAG, "Displaying LoginScreen (currentUserRole is null)")
                    LoginScreen(
                        onLoginSuccess = { role ->
                            Log.d(TAG, "onLoginSuccess callback received with role: $role")
                            currentUserRole = role
                            Log.d(TAG, "CurrentUserRole set to: $role")
                        }
                    )
                }
                UserRole.STUDENT -> {
                    Log.d(TAG, "Displaying StudentAppRoot")
                    StudentAppRoot(
                        onSwitchRole = { 
                            Log.d(TAG, "Switching to CLUB_MANAGER")
                            currentUserRole = UserRole.CLUB_MANAGER
                        },
                        onLogout = handleLogout,
                        authViewModel = authViewModel
                    )
                }
                UserRole.CLUB_MANAGER -> {
                    Log.d(TAG, "Displaying ManagerAppRoot")
                    ManagerAppRoot(
                        onSwitchRole = {
                            Log.d(TAG, "Switching to STUDENT")
                            currentUserRole = UserRole.STUDENT
                        },
                        onLogout = handleLogout,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}
