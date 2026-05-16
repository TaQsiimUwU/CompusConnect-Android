package com.taqsiim.compusconnect

import android.os.Bundle
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.taqsiim.compusconnect.data.model.UserRole
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainContent(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var currentUserRole by remember { mutableStateOf<UserRole?>(null) }
    var accountRole by remember { mutableStateOf<UserRole?>(null) }
    val authState by authViewModel.state.collectAsState()
    val currentUser = authState.currentUser

    LaunchedEffect(currentUser) {
        Log.d(TAG, "CurrentUser changed: ${currentUser?.email}, role: ${currentUser?.role}")
        if (currentUser == null) {
            currentUserRole = null
            accountRole = null
        } else if (currentUser.role != null && accountRole == null) {
            accountRole = currentUser.role
        }
        Log.d(TAG, "CurrentUserRole updated to: $currentUserRole")
    }
    
    val handleLogout: () -> Unit = {
        Log.d(TAG, "Logging out...")
        authViewModel.processIntent(AuthIntent.Logout)
        currentUserRole = null
        accountRole = null
        Log.d(TAG, "Logout complete, currentUserRole set to null")
    }

    MaterialExpressiveTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentUserRole) {
                    null -> {
                        Log.d(TAG, "Displaying LoginScreen (currentUserRole is null)")
                        LoginScreen(
                            onLoginSuccess = { role ->
                                Log.d(TAG, "onLoginSuccess callback received with role: $role")
                                currentUserRole = role
                                accountRole = role
                                Log.d(TAG, "CurrentUserRole set to: $role")
                            }
                        )
                    }
                    UserRole.STUDENT -> {
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
                    UserRole.CLUB_MANAGER -> {
                        Log.d(TAG, "Displaying ManagerAppRoot")
                        ManagerAppRoot(
                            canSwitchRole = accountRole == UserRole.CLUB_MANAGER,
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
