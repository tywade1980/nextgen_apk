package com.nextgen.apk

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * NextGen APK Application Class
 * 
 * This application serves as the central hub for:
 * - Voice-driven interactions with TTS as secondary
 * - Backend service coordination
 * - Database and vector store management
 * - MCP server integration
 * - Cross-APK integration capabilities
 */
@HiltAndroidApp
class NextGenApplication : Application() {
    
    companion object {
        private const val TAG = "NextGenApplication"
        lateinit var instance: NextGenApplication
            private set
    }
    
    // Application-wide coroutine scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        Log.i(TAG, "NextGen APK Application starting...")
        
        // Initialize core systems
        applicationScope.launch {
            initializeCoreServices()
        }
    }
    
    /**
     * Initialize all core services and components
     */
    private suspend fun initializeCoreServices() {
        try {
            Log.d(TAG, "Initializing core services...")
            
            // Initialize database layer
            initializeDatabase()
            
            // Initialize voice engine
            initializeVoiceEngine()
            
            // Initialize backend services
            initializeBackendServices()
            
            // Initialize MCP server
            initializeMCPServer()
            
            // Initialize integration hub
            initializeIntegrationHub()
            
            Log.i(TAG, "All core services initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing core services", e)
        }
    }
    
    private suspend fun initializeDatabase() {
        Log.d(TAG, "Initializing database layer...")
        // Database initialization will be handled by the database-layer module
    }
    
    private suspend fun initializeVoiceEngine() {
        Log.d(TAG, "Initializing voice engine...")
        // Voice engine initialization will be handled by the voice-engine module
    }
    
    private suspend fun initializeBackendServices() {
        Log.d(TAG, "Initializing backend services...")
        // Backend services initialization will be handled by the backend-core module
    }
    
    private suspend fun initializeMCPServer() {
        Log.d(TAG, "Initializing MCP server...")
        // MCP server initialization will be handled by the mcp-server module
    }
    
    private suspend fun initializeIntegrationHub() {
        Log.d(TAG, "Initializing integration hub...")
        // Integration hub initialization will be handled by the integration-hub module
    }
    
    /**
     * Get the application-wide coroutine scope
     */
    fun getApplicationScope(): CoroutineScope = applicationScope
}