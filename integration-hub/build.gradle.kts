plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":shared"))
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Android Integration (for APK scanning and communication)
    implementation("org.json:json:20231013")
    
    // Network Communication
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Service Discovery
    implementation("org.apache.zookeeper:zookeeper:3.9.1")
    implementation("org.apache.curator:curator-framework:5.5.0")
    implementation("org.apache.curator:curator-recipes:5.5.0")
    
    // Messaging
    implementation("org.apache.kafka:kafka-clients:3.6.1")
    implementation("io.nats:jnats:2.17.2")
    
    // Security
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.77")
    
    // File System Monitoring
    implementation("io.methvin:directory-watcher:0.18.0")
    
    // Protocol Buffers for efficient communication
    implementation("com.google.protobuf:protobuf-kotlin:3.25.1")
    
    // JSON Processing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    
    // Reflection for dynamic loading
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.mockito:mockito-core:5.7.0")
}