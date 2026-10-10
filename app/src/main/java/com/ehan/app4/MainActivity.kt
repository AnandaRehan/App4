package com.ehan.app4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.weight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ehan.app4.ui.theme.App3Theme
import com.ehan.app4.repository.SettingsRepository
import com.ehan.app4.repository.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val context: Context = LocalContext.current
            val settings = SettingsRepository(context = context)
            App3Theme(darkTheme = settings.darkTheme) {
                Greeting(
                    settings = settings
                )
            }
        }
    }
}

@Composable
fun Greeting(settings: SettingsRepository, modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Button(
                onClick = {
                    settings.systemMode()
                }
            ) {
                Text("System Theme")
            }
            Button(
                onClick = {
                    settings.lightMode()
                }
            ) {
                Text("Light Theme")
            }
            Button(
                onClick = {
                    settings.darkTheme()
                }
            ) {
                Text("Dark Theme")
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Greeting("Android")
}