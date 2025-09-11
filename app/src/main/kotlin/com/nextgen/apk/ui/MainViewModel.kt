package com.nextgen.apk.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Service status enumeration
 */
enum class ServiceStatus {
    ONLINE, OFFLINE, STARTING
}

/**
 * Main UI state data class
 */
data class MainUiState(
    val isListening: Boolean = false,
    val voiceEngineStatus: ServiceStatus = ServiceStatus.STARTING,
    val databaseStatus: ServiceStatus = ServiceStatus.STARTING,
    val backendStatus: ServiceStatus = ServiceStatus.STARTING,
    val mcpServerStatus: ServiceStatus = ServiceStatus.STARTING,
    val integrationHubStatus: ServiceStatus = ServiceStatus.STARTING,
    val recentCommands: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Main ViewModel for the NextGen APK
 * Manages UI state and coordinates with backend services
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    // Dependencies will be injected here as modules are created
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        initializeServices()
    }
    
    /**
     * Initialize all backend services and update status
     */
    private fun initializeServices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Initialize voice engine
                updateVoiceEngineStatus(ServiceStatus.ONLINE)
                
                // Initialize database
                updateDatabaseStatus(ServiceStatus.ONLINE)
                
                // Initialize backend services
                updateBackendStatus(ServiceStatus.ONLINE)
                
                // Initialize MCP server
                updateMCPServerStatus(ServiceStatus.ONLINE)
                
                // Initialize integration hub
                updateIntegrationHubStatus(ServiceStatus.ONLINE)
                
                _uiState.value = _uiState.value.copy(isLoading = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to initialize services: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Handle voice command processing
     */
    fun processVoiceCommand(command: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isListening = true)
            
            // Add command to recent commands
            val updatedCommands = listOf(command) + _uiState.value.recentCommands
            _uiState.value = _uiState.value.copy(
                recentCommands = updatedCommands.take(10) // Keep only last 10 commands
            )
            
            // Process command through backend services
            // This will be expanded as backend modules are implemented
            
            _uiState.value = _uiState.value.copy(isListening = false)
        }
    }
    
    /**
     * Update voice engine status
     */
    fun updateVoiceEngineStatus(status: ServiceStatus) {
        _uiState.value = _uiState.value.copy(voiceEngineStatus = status)
    }
    
    /**
     * Update database status
     */
    fun updateDatabaseStatus(status: ServiceStatus) {
        _uiState.value = _uiState.value.copy(databaseStatus = status)
    }
    
    /**
     * Update backend services status
     */
    fun updateBackendStatus(status: ServiceStatus) {
        _uiState.value = _uiState.value.copy(backendStatus = status)
    }
    
    /**
     * Update MCP server status
     */
    fun updateMCPServerStatus(status: ServiceStatus) {
        _uiState.value = _uiState.value.copy(mcpServerStatus = status)
    }
    
    /**
     * Update integration hub status
     */
    fun updateIntegrationHubStatus(status: ServiceStatus) {
        _uiState.value = _uiState.value.copy(integrationHubStatus = status)
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Toggle listening state
     */
    fun toggleListening() {
        _uiState.value = _uiState.value.copy(isListening = !_uiState.value.isListening)
    }
}