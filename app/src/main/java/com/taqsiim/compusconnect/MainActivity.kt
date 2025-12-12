package com.taqsiim.compusconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.taqsiim.compusconnect.ui.MainViewModel
import com.taqsiim.compusconnect.ui.navigation.ManagerAppRoot
import com.taqsiim.compusconnect.ui.navigation.StudentAppRoot
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import com.taqsiim.compusconnect.ui.theme.UserRole

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val activeRole by mainViewModel.activeRole.collectAsState()

            CampusAppTheme(userRole = activeRole) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // Switch content based on role
                        when (activeRole) {
                            UserRole.Student -> StudentAppRoot(onSwitchRole = { mainViewModel.toggleRole() })
                            UserRole.ClubManager -> ManagerAppRoot(onSwitchRole = { mainViewModel.toggleRole() })
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    CampusAppTheme(userRole = UserRole.Student) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                StudentAppRoot(onSwitchRole = {})
            }
        }
    }
}
