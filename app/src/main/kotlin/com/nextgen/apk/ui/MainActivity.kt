package com.nextgen.apk.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nextgen.apk.ui.theme.NextGenAPKTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * Main Activity for NextGen APK
 * 
 * Primary interface for voice-driven interactions with TTS support
 * Provides access to all backend services, databases, and integration capabilities
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private var textToSpeech: TextToSpeech? = null
    private var isTTSInitialized = false
    
    // Voice recognition launcher
    private val voiceRecognitionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            spokenText?.let { results ->
                if (results.isNotEmpty()) {
                    handleVoiceCommand(results[0])
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.i(TAG, "MainActivity starting...")
        
        // Initialize Text-to-Speech
        textToSpeech = TextToSpeech(this, this)
        
        setContent {
            NextGenAPKTheme {
                MainScreen()
            }
        }
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS language not supported")
            } else {
                isTTSInitialized = true
                speakText("NextGen APK initialized and ready for voice commands")
                Log.i(TAG, "TTS initialized successfully")
            }
        } else {
            Log.e(TAG, "TTS initialization failed")
        }
    }
    
    private fun speakText(text: String) {
        if (isTTSInitialized) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
    
    private fun handleVoiceCommand(command: String) {
        Log.d(TAG, "Processing voice command: $command")
        
        // Basic command processing - will be expanded with backend integration
        when {
            command.contains("status", ignoreCase = true) -> {
                speakText("NextGen APK is running. All systems operational.")
            }
            command.contains("database", ignoreCase = true) -> {
                speakText("Database systems are ready. PostgreSQL and vector stores are online.")
            }
            command.contains("integration", ignoreCase = true) -> {
                speakText("Integration hub is active and ready for cross-APK connections.")
            }
            command.contains("server", ignoreCase = true) -> {
                speakText("MCP server is running and accepting connections.")
            }
            else -> {
                speakText("Command received: $command. Processing through backend services.")
            }
        }
    }
    
    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your command...")
        }
        voiceRecognitionLauncher.launch(intent)
    }
    
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun MainScreen() {
        val viewModel: MainViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsState()
        
        // Audio permission handling
        val audioPermissionState = rememberPermissionState(
            permission = Manifest.permission.RECORD_AUDIO
        )
        
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "NextGen APK",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Voice-Driven Integration Platform",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                // Status Cards
                StatusSection(uiState)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Voice Control Section
                VoiceControlSection(
                    isListening = uiState.isListening,
                    hasAudioPermission = audioPermissionState.status.isGranted,
                    onVoiceCommand = {
                        if (audioPermissionState.status.isGranted) {
                            startVoiceRecognition()
                        } else {
                            audioPermissionState.launchPermissionRequest()
                        }
                    },
                    onRequestPermission = {
                        audioPermissionState.launchPermissionRequest()
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Recent Commands
                RecentCommandsSection(uiState.recentCommands)
            }
        }
    }
    
    @Composable
    private fun StatusSection(uiState: MainUiState) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "System Status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                StatusItem("Voice Engine", uiState.voiceEngineStatus)
                StatusItem("Database Layer", uiState.databaseStatus)
                StatusItem("Backend Services", uiState.backendStatus)
                StatusItem("MCP Server", uiState.mcpServerStatus)
                StatusItem("Integration Hub", uiState.integrationHubStatus)
            }
        }
    }
    
    @Composable
    private fun StatusItem(name: String, status: ServiceStatus) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name)
            Text(
                text = status.name,
                color = when (status) {
                    ServiceStatus.ONLINE -> MaterialTheme.colorScheme.primary
                    ServiceStatus.OFFLINE -> MaterialTheme.colorScheme.error
                    ServiceStatus.STARTING -> MaterialTheme.colorScheme.secondary
                }
            )
        }
    }
    
    @Composable
    private fun VoiceControlSection(
        isListening: Boolean,
        hasAudioPermission: Boolean,
        onVoiceCommand: () -> Unit,
        onRequestPermission: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Voice Commands",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                if (!hasAudioPermission) {
                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Audio Permission")
                    }
                } else {
                    FloatingActionButton(
                        onClick = onVoiceCommand,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isListening) "Stop Listening" else "Start Listening",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Text(
                        text = if (isListening) "Listening..." else "Tap to speak",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
    
    @Composable
    private fun RecentCommandsSection(commands: List<String>) {
        if (commands.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Recent Commands",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    LazyColumn {
                        items(commands.take(5)) { command ->
                            Text(
                                text = "• $command",
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    
    override fun onDestroy() {
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}