package com.taqsiim.compusconnect

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.ui.theme.CompusConnectTheme
import com.taqsiim.compusconnect.ui.auth.LoginScreen
import com.taqsiim.compusconnect.ui.navigation.ManagerAppRoot
import com.taqsiim.compusconnect.ui.navigation.StudentAppRoot
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
            MainContent()
        }
    }
}

@Composable
fun MainContent(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var currentUserRole by remember { mutableStateOf<UserRole?>(null) }
    var accountRole by remember { mutableStateOf<UserRole?>(null) }
    val authState by authViewModel.state.collectAsState()
    val currentUser = authState.currentUser
    val sessionReady = !authState.isCheckingSession
    val effectiveAccountRole = accountRole ?: currentUser?.role?.takeIf { sessionReady }

    val handleLogout: () -> Unit = {
        Log.d(TAG, "Logging out...")
        authViewModel.processIntent(AuthIntent.Logout)
        currentUserRole = null
        accountRole = null
        Log.d(TAG, "Logout complete, currentUserRole set to null")
    }

    CompusConnectTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    !sessionReady -> {
                        CircularProgressIndicator()
                    }
                    displayRole == null -> {
                        Log.d(TAG, "Displaying LoginScreen")
                        LoginScreen(
                            onLoginSuccess = { role ->
                                Log.d(TAG, "onLoginSuccess callback received with role: $role")
                                currentUserRole = role
                                accountRole = role
                            }
                        )
                    }
                    displayRole == UserRole.STUDENT -> {
                        Log.d(TAG, "Displaying StudentAppRoot")
                        StudentAppRoot(
                            canSwitchRole = accountRole == UserRole.CLUB_MANAGER,
                            onSwitchRole = {
                                Log.d(TAG, "Switching to CLUB_MANAGER")
                                currentUserRole = UserRole.CLUB_MANAGER
                            },
                            onLogout = handleLogout,
                            authViewModel = authViewModel
                        )
                    }
                    displayRole == UserRole.CLUB_MANAGER -> {
                        Log.d(TAG, "Displaying ManagerAppRoot")
                        ManagerAppRoot(
                            canSwitchRole = effectiveAccountRole == UserRole.CLUB_MANAGER,
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
}
