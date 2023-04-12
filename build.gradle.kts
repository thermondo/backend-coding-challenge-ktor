import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.7.21"
val ktorVersion = "2.2.3"
val logbackVersion = "1.2.3"
val h2Version = "2.1.214"
val exposedVersion = "0.38.2"
val hikariVersion = "5.0.1"
val koinVersion = "3.2.0"
val mockkVersion = "1.12.8"
val ktorSwagger = "1.5.0"

group = "com.thermondo"
version = System.getenv("VERSION") ?: "local"

plugins {
    application
    kotlin("jvm").version("1.7.21")
    kotlin("plugin.serialization") version "1.7.21"

    // Quality gate
    id("org.jmailen.kotlinter").version("3.7.0")
    id("io.gitlab.arturbosch.detekt").version("1.19.0")
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:${ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${ktorVersion}")
    implementation("io.ktor:ktor-server-auth:${ktorVersion}")
    implementation("io.ktor:ktor-server-auth-jwt:${ktorVersion}")
    implementation("io.ktor:ktor-server-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-jackson:${ktorVersion}")
    implementation("io.ktor:ktor-server-default-headers:${ktorVersion}")
    implementation("io.ktor:ktor-server-cors:${ktorVersion}")
    implementation("io.ktor:ktor-server-call-logging:${ktorVersion}")
    implementation("io.ktor:ktor-server-status-pages:${ktorVersion}")
    implementation("io.insert-koin:koin-ktor:${koinVersion}")
    implementation ("io.github.smiley4:ktor-swagger-ui:${ktorSwagger}")

    // Database
    implementation("com.h2database:h2:${h2Version}")
    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-java-time:${exposedVersion}")
    implementation("com.zaxxer:HikariCP:${hikariVersion}")

    // Logback
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")

    // Testing
    // Kotlin
    testImplementation("org.jetbrains.kotlin:kotlin-test:${kotlinVersion}")

    // Ktor
    testImplementation("io.ktor:ktor-server-tests:${ktorVersion}")
    testImplementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
    testImplementation("io.ktor:ktor-server-test-host:${ktorVersion}")
    testImplementation("io.mockk:mockk:${mockkVersion}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.test {
    useJUnit()
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "11"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "11"
}

tasks.withType<Jar> {
    manifest {
        attributes("Main-Class" to application.mainClass)
    }
}
