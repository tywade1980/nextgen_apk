package agents

import shared.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * HermesBrain - Advanced Communication and Translation Agent
 * Responsible for inter-agent communication, language processing, and protocol translation
 */
class HermesBrain(
    override val id: String = "hermes-001",
    private val config: AgentConfig
) : Agent {
    
    override val type = AgentType.HERMES_BRAIN
    override var state = AgentState.IDLE
        private set
    
    private val communicationChannels = mutableMapOf<String, CommunicationChannel>()
    private val messageQueue = mutableListOf<QueuedMessage>()
    private val translationCache = mutableMapOf<String, TranslationEntry>()
    private val protocolHandlers = mutableMapOf<CommunicationProtocol, ProtocolHandler>()
    
    private var isActive = false
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    data class CommunicationChannel(
        val id: String,
        val participants: Set<AgentType>,
        val protocol: CommunicationProtocol,
        val status: ChannelStatus,
        val messageHistory: MutableList<Message>,
        val bandwidth: Float,
        val latency: Float
    )
    
    enum class CommunicationProtocol {
        DIRECT_MESSAGE,
        BROADCAST,
        MULTICAST,
        SECURE_TUNNEL,
        MESH_NETWORK,
        HIERARCHICAL
    }
    
    enum class ChannelStatus {
        ACTIVE,
        IDLE,
        CONGESTED,
        FAILED,
        MAINTENANCE
    }
    
    data class QueuedMessage(
        val message: Message,
        val channel: String,
        val retryCount: Int = 0,
        val queueTime: Long = System.currentTimeMillis()
    )
    
    data class TranslationEntry(
        val source: String,
        val target: String,
        val sourceProtocol: CommunicationProtocol,
        val targetProtocol: CommunicationProtocol,
        val confidence: Float,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    interface ProtocolHandler {
        suspend fun encode(message: Message): ByteArray
        suspend fun decode(data: ByteArray): Message
        fun getMetadata(): Map<String, String>
    }
    
    init {
        initializeCommunicationChannels()
        setupProtocolHandlers()
        startMessageProcessing()
    }
    
    override suspend fun process(message: Message): Message? {
        state = AgentState.COMMUNICATING
        
        return try {
            when (message.content) {
                "ESTABLISH_CHANNEL" -> establishChannel(message)
                "TRANSLATE_MESSAGE" -> translateMessage(message)
                "BROADCAST_MESSAGE" -> broadcastMessage(message)
                "ROUTE_MESSAGE" -> routeMessage(message)
                "CHANNEL_STATUS" -> getChannelStatus(message)
                "OPTIMIZE_COMMUNICATION" -> optimizeCommunication(message)
                else -> relayMessage(message)
            }
        } catch (e: Exception) {
            state = AgentState.ERROR
            Message(
                id = generateMessageId(),
                fromAgent = type,
                toAgent = message.fromAgent,
                content = "COMMUNICATION_ERROR: ${e.message}",
                priority = Priority.HIGH
            )
        } finally {
            state = AgentState.IDLE
        }
    }
    
    override suspend fun learn(event: LearningEvent) {
        when (event.eventType) {
            LearningType.PATTERN_RECOGNITION -> {
                // Learn communication patterns for optimization
                analyzeCommunicationPatterns()
            }
            LearningType.BEHAVIOR_ADAPTATION -> {
                // Adapt routing and translation strategies
                adaptCommunicationStrategy(event.improvement)
            }
            LearningType.KNOWLEDGE_INTEGRATION -> {
                // Integrate new protocol knowledge
                integrateProtocolKnowledge(event.data)
            }
            LearningType.SKILL_ACQUISITION -> {
                // Learn new communication techniques
                learnCommunicationTechnique(event.data)
            }
        }
    }
    
    override fun getCapabilities(): List<String> {
        return listOf(
            "Multi-Protocol Communication",
            "Real-time Translation",
            "Message Routing",
            "Channel Management",
            "Encryption/Decryption",
            "Load Balancing",
            "Error Recovery",
            "Communication Optimization",
            "Protocol Bridging",
            "Semantic Understanding"
        )
    }
    
    fun start() {
        isActive = true
        startMessageProcessing()
        startChannelMonitoring()
        startCommunicationOptimization()
    }
    
    fun stop() {
        isActive = false
        scope.cancel()
    }
    
    private fun initializeCommunicationChannels() {
        // Create default channels for each agent type
        AgentType.values().forEach { agentType ->
            if (agentType != AgentType.HERMES_BRAIN) {
                val channelId = "channel-${agentType.name.lowercase()}"
                communicationChannels[channelId] = CommunicationChannel(
                    id = channelId,
                    participants = setOf(AgentType.HERMES_BRAIN, agentType),
                    protocol = CommunicationProtocol.DIRECT_MESSAGE,
                    status = ChannelStatus.ACTIVE,
                    messageHistory = mutableListOf(),
                    bandwidth = 100f,
                    latency = 10f
                )
            }
        }
        
        // Create broadcast channel
        communicationChannels["broadcast"] = CommunicationChannel(
            id = "broadcast",
            participants = AgentType.values().toSet(),
            protocol = CommunicationProtocol.BROADCAST,
            status = ChannelStatus.ACTIVE,
            messageHistory = mutableListOf(),
            bandwidth = 1000f,
            latency = 5f
        )
    }
    
    private fun setupProtocolHandlers() {
        protocolHandlers[CommunicationProtocol.DIRECT_MESSAGE] = DirectMessageHandler()
        protocolHandlers[CommunicationProtocol.BROADCAST] = BroadcastHandler()
        protocolHandlers[CommunicationProtocol.MULTICAST] = MulticastHandler()
        protocolHandlers[CommunicationProtocol.SECURE_TUNNEL] = SecureTunnelHandler()
        protocolHandlers[CommunicationProtocol.MESH_NETWORK] = MeshNetworkHandler()
        protocolHandlers[CommunicationProtocol.HIERARCHICAL] = HierarchicalHandler()
    }
    
    private fun startMessageProcessing() {
        scope.launch {
            while (isActive) {
                processMessageQueue()
                delay(100) // Process every 100ms
            }
        }
    }
    
    private fun startChannelMonitoring() {
        scope.launch {
            while (isActive) {
                monitorChannelHealth()
                updateChannelMetrics()
                delay(5000) // Monitor every 5 seconds
            }
        }
    }
    
    private fun startCommunicationOptimization() {
        scope.launch {
            while (isActive) {
                optimizeChannelPerformance()
                cleanupOldMessages()
                delay(30000) // Optimize every 30 seconds
            }
        }
    }
    
    private suspend fun establishChannel(message: Message): Message {
        val participants = message.metadata["participants"]?.split(",")?.map { 
            AgentType.valueOf(it.trim().uppercase()) 
        }?.toSet() ?: return createErrorMessage(message, "Invalid participants")
        
        val protocol = message.metadata["protocol"]?.let { 
            CommunicationProtocol.valueOf(it.uppercase()) 
        } ?: CommunicationProtocol.DIRECT_MESSAGE
        
        val channelId = "channel-${System.currentTimeMillis()}"
        val channel = CommunicationChannel(
            id = channelId,
            participants = participants,
            protocol = protocol,
            status = ChannelStatus.ACTIVE,
            messageHistory = mutableListOf(),
            bandwidth = 100f,
            latency = calculateLatency(participants.size, protocol)
        )
        
        communicationChannels[channelId] = channel
        
        return Message(
            id = generateMessageId(),
            fromAgent = type,
            toAgent = message.fromAgent,
            content = "CHANNEL_ESTABLISHED",
            metadata = mapOf(
                "channel_id" to channelId,
                "protocol" to protocol.name,
                "participants" to participants.joinToString(",") { it.name }
            )
        )
    }
    
    private suspend fun translateMessage(message: Message): Message {
        val sourceProtocol = message.metadata["source_protocol"]?.let { 
            CommunicationProtocol.valueOf(it.uppercase()) 
        } ?: CommunicationProtocol.DIRECT_MESSAGE
        
        val targetProtocol = message.metadata["target_protocol"]?.let { 
            CommunicationProtocol.valueOf(it.uppercase()) 
        } ?: CommunicationProtocol.DIRECT_MESSAGE
        
        val originalContent = message.metadata["content"] ?: message.content
        
        val translated = performTranslation(originalContent, sourceProtocol, targetProtocol)
        
        // Cache translation for future use
        val cacheKey = "${originalContent.hashCode()}-${sourceProtocol.name}-${targetProtocol.name}"
        translationCache[cacheKey] = TranslationEntry(
            source = originalContent,
            target = translated,
            sourceProtocol = sourceProtocol,
            targetProtocol = targetProtocol,
            confidence = 0.95f
        )
        
        return Message(
            id = generateMessageId(),
            fromAgent = type,
            toAgent = message.fromAgent,
            content = translated,
            metadata = mapOf(
                "translation_confidence" to "0.95",
                "source_protocol" to sourceProtocol.name,
                "target_protocol" to targetProtocol.name
            )
        )
    }
    
    private suspend fun broadcastMessage(message: Message): Message {
        val broadcastChannel = communicationChannels["broadcast"]
            ?: return createErrorMessage(message, "Broadcast channel not available")
        
        val broadcastMessage = message.copy(
            id = generateMessageId(),
            fromAgent = type,
            toAgent = null // Broadcast to all
        )
        
        // Add to all agent-specific channels
        communicationChannels.values.forEach { channel ->
            if (channel.protocol == CommunicationProtocol.BROADCAST || 
                channel.participants.contains(message.fromAgent)) {
                channel.messageHistory.add(broadcastMessage)
            }
        }
        
        return Message(
            id = generateMessageId(),
            fromAgent = type,
            toAgent = message.fromAgent,
            content = "BROADCAST_COMPLETE",
            metadata = mapOf(
                "recipients" to broadcastChannel.participants.size.toString(),
                "message_id" to broadcastMessage.id
            )
        )
    }
    
    private suspend fun routeMessage(message: Message): Message {
        val targetAgent = message.toAgent
            ?: return createErrorMessage(message, "No target agent specified")
        
        val optimalChannel = findOptimalChannel(message.fromAgent, targetAgent)
            ?: return createErrorMessage(message, "No available channel to target agent")
        
        val routedMessage = message.copy(id = generateMessageId())
        optimalChannel.messageHistory.add(routedMessage)
        
        // Add to processing queue
        messageQueue.add(QueuedMessage(routedMessage, optimalChannel.id))
        
        return Message(
            id = generateMessageId(),
            fromAgent = type,
            toAgent = message.fromAgent,
            content = "MESSAGE_ROUTED",
            metadata = mapOf(
                "channel_id" to optimalChannel.id,
                "estimated_delivery_time" to optimalChannel.latency.toString(),
                "routed_message_id" to routedMessage.id
            )
        )
    }
    
    private suspend fun getChannelStatus(message: Message): Message {
        val channelId = message.metadata["channel_id"]
        
        if (channelId != null) {
            val channel = communicationChannels[channelId]
                ?: return createErrorMessage(message, "Channel not found")
            
            return Message(
                id = generateMessageId(),
                fromAgent = type,
                toAgent = message.fromAgent,
                content = "CHANNEL_STATUS",
                metadata = mapOf(
                    "channel_id" to channel.id,
                    "status" to channel.status.name,
                    "participants" to channel.participants.size.toString(),
                    "message_count" to channel.messageHistory.size.toString(),
                    "bandwidth" to channel.bandwidth.toString(),
                    "latency" to channel.latency.toString()
                )
            )
        } else {
            // Return status of all channels
            val statusReport = communicationChannels.values.joinToString("\n") { channel ->
                "${channel.id}: ${channel.status.name} (${channel.participants.size} participants, ${channel.messageHistory.size} messages)"
            }
            
            return Message(
                id = generateMessageId(),
                fromAgent = type,
                toAgent = message.fromAgent,
                content = "ALL_CHANNELS_STATUS:\n$statusReport"
            )
        }
    }
    
    private suspend fun optimizeCommunication(message: Message): Message {
        var optimizations = 0
        
        // Optimize channel bandwidth allocation
        communicationChannels.values.forEach { channel ->
            if (channel.status == ChannelStatus.CONGESTED) {
                // Increase bandwidth or create alternate routes
                optimizations++
            }
        }
        
        // Clean up idle channels
        val idleChannels = communicationChannels.values.filter { 
            it.status == ChannelStatus.IDLE && 
            it.messageHistory.none { msg -> System.currentTimeMillis() - msg.timestamp < 300000 }
        }
        optimizations += idleChannels.size
        
        // Optimize translation cache
        val oldTranslations = translationCache.values.filter {
            System.currentTimeMillis() - it.timestamp > 3600000 // 1 hour old
        }
        optimizations += oldTranslations.size
        
        return Message(
            id = generateMessageId(),
            fromAgent = type,
            toAgent = message.fromAgent,
            content = "COMMUNICATION_OPTIMIZED",
            metadata = mapOf(
                "optimizations_performed" to optimizations.toString(),
                "active_channels" to communicationChannels.size.toString(),
                "queued_messages" to messageQueue.size.toString()
            )
        )
    }
    
    private suspend fun relayMessage(message: Message): Message? {
        // If this is a relay request, forward the message to its destination
        val targetAgent = message.toAgent ?: return null
        
        val channel = findOptimalChannel(message.fromAgent, targetAgent)
        if (channel != null) {
            channel.messageHistory.add(message)
            messageQueue.add(QueuedMessage(message, channel.id))
        }
        
        return null // No response needed for relays
    }
    
    private suspend fun processMessageQueue() {
        val messagesToProcess = messageQueue.toList()
        messageQueue.clear()
        
        messagesToProcess.forEach { queuedMessage ->
            try {
                val channel = communicationChannels[queuedMessage.channel]
                if (channel != null && channel.status == ChannelStatus.ACTIVE) {
                    // Simulate message delivery
                    delay(channel.latency.toLong())
                    // Message delivered successfully
                } else {
                    // Channel unavailable, retry if possible
                    if (queuedMessage.retryCount < 3) {
                        messageQueue.add(queuedMessage.copy(retryCount = queuedMessage.retryCount + 1))
                    }
                }
            } catch (e: Exception) {
                // Handle delivery failure
                if (queuedMessage.retryCount < 3) {
                    messageQueue.add(queuedMessage.copy(retryCount = queuedMessage.retryCount + 1))
                }
            }
        }
    }
    
    private fun monitorChannelHealth() {
        communicationChannels.values.forEach { channel ->
            // Simulate channel health monitoring
            val healthScore = calculateChannelHealth(channel)
            
            channel.status = when {
                healthScore > 0.8f -> ChannelStatus.ACTIVE
                healthScore > 0.5f -> ChannelStatus.IDLE
                healthScore > 0.2f -> ChannelStatus.CONGESTED
                else -> ChannelStatus.FAILED
            }
        }
    }
    
    private fun updateChannelMetrics() {
        communicationChannels.values.forEach { channel ->
            // Update bandwidth and latency based on usage
            val recentMessages = channel.messageHistory.filter {
                System.currentTimeMillis() - it.timestamp < 60000 // Last minute
            }
            
            // Adjust bandwidth based on load
            val load = recentMessages.size / 60f // messages per second
            channel.bandwidth = (100f - load * 10f).coerceAtLeast(10f)
            
            // Adjust latency based on congestion
            channel.latency = when {
                load > 5f -> 50f
                load > 2f -> 20f
                else -> 10f
            }
        }
    }
    
    private suspend fun optimizeChannelPerformance() {
        // Implement channel optimization algorithms
        communicationChannels.values.forEach { channel ->
            when (channel.status) {
                ChannelStatus.CONGESTED -> {
                    // Try to reduce congestion
                    redistributeLoad(channel)
                }
                ChannelStatus.FAILED -> {
                    // Attempt recovery
                    attemptChannelRecovery(channel)
                }
                else -> {
                    // Normal optimization
                    tuneChannelParameters(channel)
                }
            }
        }
    }
    
    private fun cleanupOldMessages() {
        val cutoffTime = System.currentTimeMillis() - 3600000 // 1 hour ago
        
        communicationChannels.values.forEach { channel ->
            channel.messageHistory.removeAll { it.timestamp < cutoffTime }
        }
        
        // Clean up translation cache
        val expiredTranslations = translationCache.filterValues { 
            System.currentTimeMillis() - it.timestamp > 3600000 
        }
        expiredTranslations.keys.forEach { translationCache.remove(it) }
    }
    
    private fun performTranslation(content: String, source: CommunicationProtocol, target: CommunicationProtocol): String {
        // Implement protocol translation logic
        return when (source to target) {
            CommunicationProtocol.DIRECT_MESSAGE to CommunicationProtocol.BROADCAST -> 
                "[BROADCAST] $content"
            CommunicationProtocol.BROADCAST to CommunicationProtocol.DIRECT_MESSAGE -> 
                content.removePrefix("[BROADCAST] ")
            CommunicationProtocol.SECURE_TUNNEL to CommunicationProtocol.DIRECT_MESSAGE ->
                "[DECRYPTED] $content"
            else -> content
        }
    }
    
    private fun findOptimalChannel(from: AgentType, to: AgentType): CommunicationChannel? {
        return communicationChannels.values
            .filter { it.participants.contains(from) && it.participants.contains(to) }
            .filter { it.status == ChannelStatus.ACTIVE }
            .minByOrNull { it.latency + (100f - it.bandwidth) }
    }
    
    private fun calculateLatency(participantCount: Int, protocol: CommunicationProtocol): Float {
        val baseLatency = when (protocol) {
            CommunicationProtocol.DIRECT_MESSAGE -> 5f
            CommunicationProtocol.BROADCAST -> 10f
            CommunicationProtocol.MULTICAST -> 15f
            CommunicationProtocol.SECURE_TUNNEL -> 25f
            CommunicationProtocol.MESH_NETWORK -> 20f
            CommunicationProtocol.HIERARCHICAL -> 30f
        }
        return baseLatency + (participantCount * 2f)
    }
    
    private fun calculateChannelHealth(channel: CommunicationChannel): Float {
        val messageLoad = channel.messageHistory.size / 1000f
        val bandwidthUtilization = (100f - channel.bandwidth) / 100f
        val latencyPenalty = channel.latency / 100f
        
        return (1f - messageLoad - bandwidthUtilization - latencyPenalty).coerceIn(0f, 1f)
    }
    
    private fun redistributeLoad(channel: CommunicationChannel) {
        // Implement load redistribution logic
    }
    
    private fun attemptChannelRecovery(channel: CommunicationChannel) {
        // Implement channel recovery logic
    }
    
    private fun tuneChannelParameters(channel: CommunicationChannel) {
        // Implement parameter tuning logic
    }
    
    private fun analyzeCommunicationPatterns() {
        // Analyze communication patterns for optimization
    }
    
    private fun adaptCommunicationStrategy(improvement: Float) {
        // Adapt communication strategy based on learning
    }
    
    private fun integrateProtocolKnowledge(data: String) {
        // Integrate new protocol knowledge
    }
    
    private fun learnCommunicationTechnique(data: String) {
        // Learn new communication techniques
    }
    
    private fun createErrorMessage(originalMessage: Message, error: String): Message {
        return Message(
            id = generateMessageId(),
            fromAgent = type,
            toAgent = originalMessage.fromAgent,
            content = "COMMUNICATION_ERROR: $error",
            priority = Priority.HIGH
        )
    }
    
    private fun generateMessageId(): String {
        return "hermes-msg-${System.currentTimeMillis()}-${(1000..9999).random()}"
    }
    
    // Protocol Handler Implementations
    private inner class DirectMessageHandler : ProtocolHandler {
        override suspend fun encode(message: Message): ByteArray {
            return message.content.toByteArray()
        }
        
        override suspend fun decode(data: ByteArray): Message {
            return Message(
                id = generateMessageId(),
                fromAgent = AgentType.HERMES_BRAIN,
                toAgent = null,
                content = String(data)
            )
        }
        
        override fun getMetadata(): Map<String, String> {
            return mapOf("protocol" to "DIRECT_MESSAGE", "encryption" to "none")
        }
    }
    
    private inner class BroadcastHandler : ProtocolHandler {
        override suspend fun encode(message: Message): ByteArray {
            return "[BROADCAST]${message.content}".toByteArray()
        }
        
        override suspend fun decode(data: ByteArray): Message {
            val content = String(data).removePrefix("[BROADCAST]")
            return Message(
                id = generateMessageId(),
                fromAgent = AgentType.HERMES_BRAIN,
                toAgent = null,
                content = content
            )
        }
        
        override fun getMetadata(): Map<String, String> {
            return mapOf("protocol" to "BROADCAST", "multicast" to "true")
        }
    }
    
    private inner class MulticastHandler : ProtocolHandler {
        override suspend fun encode(message: Message): ByteArray {
            return "[MULTICAST]${message.content}".toByteArray()
        }
        
        override suspend fun decode(data: ByteArray): Message {
            val content = String(data).removePrefix("[MULTICAST]")
            return Message(
                id = generateMessageId(),
                fromAgent = AgentType.HERMES_BRAIN,
                toAgent = null,
                content = content
            )
        }
        
        override fun getMetadata(): Map<String, String> {
            return mapOf("protocol" to "MULTICAST", "selective" to "true")
        }
    }
    
    private inner class SecureTunnelHandler : ProtocolHandler {
        override suspend fun encode(message: Message): ByteArray {
            // Simulate encryption
            return "[ENCRYPTED]${message.content}".toByteArray()
        }
        
        override suspend fun decode(data: ByteArray): Message {
            val content = String(data).removePrefix("[ENCRYPTED]")
            return Message(
                id = generateMessageId(),
                fromAgent = AgentType.HERMES_BRAIN,
                toAgent = null,
                content = content
            )
        }
        
        override fun getMetadata(): Map<String, String> {
            return mapOf("protocol" to "SECURE_TUNNEL", "encryption" to "AES256")
        }
    }
    
    private inner class MeshNetworkHandler : ProtocolHandler {
        override suspend fun encode(message: Message): ByteArray {
            return "[MESH]${message.content}".toByteArray()
        }
        
        override suspend fun decode(data: ByteArray): Message {
            val content = String(data).removePrefix("[MESH]")
            return Message(
                id = generateMessageId(),
                fromAgent = AgentType.HERMES_BRAIN,
                toAgent = null,
                content = content
            )
        }
        
        override fun getMetadata(): Map<String, String> {
            return mapOf("protocol" to "MESH_NETWORK", "routing" to "dynamic")
        }
    }
    
    private inner class HierarchicalHandler : ProtocolHandler {
        override suspend fun encode(message: Message): ByteArray {
            return "[HIERARCHICAL]${message.content}".toByteArray()
        }
        
        override suspend fun decode(data: ByteArray): Message {
            val content = String(data).removePrefix("[HIERARCHICAL]")
            return Message(
                id = generateMessageId(),
                fromAgent = AgentType.HERMES_BRAIN,
                toAgent = null,
                content = content
            )
        }
        
        override fun getMetadata(): Map<String, String> {
            return mapOf("protocol" to "HIERARCHICAL", "tree_based" to "true")
        }
    }
}