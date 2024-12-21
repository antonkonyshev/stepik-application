package com.github.antonkonyshev.stepic.presentation

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.github.antonkonyshev.stepic.presentation.navigation.BottomNavigationBar
import com.github.antonkonyshev.stepic.presentation.navigation.StepicNavHost
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val _eventBus = MutableSharedFlow<UiEvent>()
    val eventBus = _eventBus.asSharedFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StepicTheme(dynamicColor = false) {
                val navController = rememberNavController()

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        BottomNavigationBar(navController)
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    StepicNavHost(navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    fun emitUiEvent(id: String, extras: String = "") {
        lifecycleScope.launch { _eventBus.emit(UiEvent(id, extras)) }
    }

    fun emitUiEvent(uiEvent: UiEvent) {
        lifecycleScope.launch { _eventBus.emit(uiEvent) }
    }
}

fun Context.getActivity(): MainActivity? = when (this) {
    is MainActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

data class UiEvent(val id: String, val extra: String = "")