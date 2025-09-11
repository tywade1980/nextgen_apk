package com.nextgen.database.repository

import com.nextgen.database.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * Repository for Voice Commands
 */
@Repository
interface VoiceCommandRepository : JpaRepository<VoiceCommandEntity, String> {
    
    fun findByProcessedOrderByTimestampDesc(processed: Boolean): List<VoiceCommandEntity>
    
    fun findTop10ByOrderByTimestampDesc(): List<VoiceCommandEntity>
    
    @Query("SELECT v FROM VoiceCommandEntity v WHERE v.timestamp >= :since ORDER BY v.timestamp DESC")
    fun findRecentCommands(@Param("since") since: LocalDateTime): List<VoiceCommandEntity>
    
    fun findByTextContainingIgnoreCase(text: String): List<VoiceCommandEntity>
    
    @Query("SELECT COUNT(v) FROM VoiceCommandEntity v WHERE v.timestamp >= :since")
    fun countCommandsSince(@Param("since") since: LocalDateTime): Long
}

/**
 * Repository for Integration Connections
 */
@Repository
interface IntegrationConnectionRepository : JpaRepository<IntegrationConnectionEntity, String> {
    
    fun findByIsActiveTrue(): List<IntegrationConnectionEntity>
    
    fun findByTargetPackage(targetPackage: String): IntegrationConnectionEntity?
    
    fun findByConnectionType(connectionType: IntegrationConnectionEntity.ConnectionType): List<IntegrationConnectionEntity>
    
    @Query("SELECT i FROM IntegrationConnectionEntity i WHERE i.lastCommunication >= :since")
    fun findActiveConnections(@Param("since") since: LocalDateTime): List<IntegrationConnectionEntity>
}

/**
 * Repository for MCP Messages
 */
@Repository
interface MCPMessageRepository : JpaRepository<MCPMessageEntity, String> {
    
    fun findByProcessedOrderByTimestampDesc(processed: Boolean): List<MCPMessageEntity>
    
    fun findBySourceOrderByTimestampDesc(source: String): List<MCPMessageEntity>
    
    fun findByTargetOrderByTimestampDesc(target: String): List<MCPMessageEntity>
    
    fun findByTypeOrderByTimestampDesc(type: MCPMessageEntity.MessageType): List<MCPMessageEntity>
    
    @Query("SELECT m FROM MCPMessageEntity m WHERE m.timestamp >= :since ORDER BY m.timestamp DESC")
    fun findRecentMessages(@Param("since") since: LocalDateTime): List<MCPMessageEntity>
}

/**
 * Repository for Vector Entries
 */
@Repository
interface VectorEntryRepository : JpaRepository<VectorEntryEntity, String> {
    
    fun findByDocumentType(documentType: String): List<VectorEntryEntity>
    
    fun findByDocumentId(documentId: String): List<VectorEntryEntity>
    
    @Query(value = """
        SELECT *, (embedding <-> CAST(:queryVector AS vector)) AS distance 
        FROM vector_entries 
        ORDER BY distance 
        LIMIT :limit
    """, nativeQuery = true)
    fun findSimilarVectors(
        @Param("queryVector") queryVector: String,
        @Param("limit") limit: Int
    ): List<VectorEntryEntity>
    
    @Query(value = """
        SELECT *, (embedding <-> CAST(:queryVector AS vector)) AS distance 
        FROM vector_entries 
        WHERE document_type = :documentType
        ORDER BY distance 
        LIMIT :limit
    """, nativeQuery = true)
    fun findSimilarVectorsByType(
        @Param("queryVector") queryVector: String,
        @Param("documentType") documentType: String,
        @Param("limit") limit: Int
    ): List<VectorEntryEntity>
}

/**
 * Repository for Service Status
 */
@Repository
interface ServiceStatusRepository : JpaRepository<ServiceStatusEntity, String> {
    
    fun findByServiceName(serviceName: String): ServiceStatusEntity?
    
    fun findByStatus(status: ServiceStatusEntity.Status): List<ServiceStatusEntity>
    
    @Query("SELECT s FROM ServiceStatusEntity s ORDER BY s.lastUpdate DESC")
    fun findAllOrderByLastUpdate(): List<ServiceStatusEntity>
    
    @Query("SELECT COUNT(s) FROM ServiceStatusEntity s WHERE s.status = :status")
    fun countByStatus(@Param("status") status: ServiceStatusEntity.Status): Long
}

/**
 * Repository for Database Operations
 */
@Repository
interface DatabaseOperationRepository : JpaRepository<DatabaseOperationEntity, String> {
    
    fun findBySuccessOrderByTimestampDesc(success: Boolean): List<DatabaseOperationEntity>
    
    fun findByOperationOrderByTimestampDesc(operation: DatabaseOperationEntity.Operation): List<DatabaseOperationEntity>
    
    fun findByTableNameOrderByTimestampDesc(tableName: String): List<DatabaseOperationEntity>
    
    @Query("SELECT d FROM DatabaseOperationEntity d WHERE d.timestamp >= :since ORDER BY d.timestamp DESC")
    fun findRecentOperations(@Param("since") since: LocalDateTime): List<DatabaseOperationEntity>
    
    @Query("SELECT AVG(d.executionTimeMs) FROM DatabaseOperationEntity d WHERE d.success = true AND d.timestamp >= :since")
    fun getAverageExecutionTime(@Param("since") since: LocalDateTime): Double?
}