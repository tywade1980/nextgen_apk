package com.nextgen.database.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Voice Command Entity for database storage
 */
@Entity
@Table(name = "voice_commands")
data class VoiceCommandEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Column(nullable = false, columnDefinition = "TEXT")
    val text: String,
    
    @Column(nullable = false)
    val confidence: Float,
    
    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val language: String = "en-US",
    
    @Column(nullable = false)
    val processed: Boolean = false,
    
    @Column(columnDefinition = "TEXT")
    val response: String? = null,
    
    @Column(name = "processing_time_ms")
    val processingTimeMs: Long? = null
)

/**
 * Integration Connection Entity
 */
@Entity
@Table(name = "integration_connections")
data class IntegrationConnectionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Column(nullable = false, unique = true)
    val targetPackage: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val connectionType: ConnectionType,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column
    val lastCommunication: LocalDateTime? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    enum class ConnectionType {
        SERVICE_BINDING, CONTENT_PROVIDER, BROADCAST, INTENT
    }
}

/**
 * MCP Message Entity
 */
@Entity
@Table(name = "mcp_messages")
data class MCPMessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: MessageType,
    
    @Column(nullable = false)
    val source: String,
    
    @Column(nullable = false)
    val target: String,
    
    @Column(nullable = false, columnDefinition = "TEXT")
    val payload: String,
    
    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    @Column
    val processed: Boolean = false
) {
    enum class MessageType {
        COMMAND, RESPONSE, EVENT, HEARTBEAT
    }
}

/**
 * Vector Entry Entity for vector database operations
 */
@Entity
@Table(name = "vector_entries")
data class VectorEntryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Column(nullable = false, columnDefinition = "vector(1536)")
    val embedding: String, // Will store as vector type in PostgreSQL with pgvector
    
    @Column(nullable = false, columnDefinition = "JSONB")
    val metadata: String, // JSON metadata
    
    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    @Column
    val documentId: String? = null,
    
    @Column
    val documentType: String? = null
)

/**
 * Service Status Entity
 */
@Entity
@Table(name = "service_status")
data class ServiceStatusEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Column(nullable = false, unique = true)
    val serviceName: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: Status,
    
    @Column(nullable = false)
    val lastUpdate: LocalDateTime = LocalDateTime.now(),
    
    @Column(columnDefinition = "TEXT")
    val details: String? = null,
    
    @Column
    val uptime: Long? = null
) {
    enum class Status {
        ONLINE, OFFLINE, STARTING, ERROR
    }
}

/**
 * Database Operation Log Entity
 */
@Entity
@Table(name = "database_operations")
data class DatabaseOperationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val operation: Operation,
    
    @Column(nullable = false)
    val tableName: String,
    
    @Column(nullable = false, columnDefinition = "JSONB")
    val data: String, // JSON data
    
    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val success: Boolean = false,
    
    @Column(columnDefinition = "TEXT")
    val errorMessage: String? = null,
    
    @Column
    val executionTimeMs: Long? = null
) {
    enum class Operation {
        CREATE, READ, UPDATE, DELETE, QUERY
    }
}