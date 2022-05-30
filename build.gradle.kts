import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.thermondo"
version = System.getenv("VERSION") ?: "local"

plugins {
    application
    kotlin("jvm").version("1.6.0")
    kotlin("plugin.serialization") version "1.6.20"

    // Quality gate
    id("org.jmailen.kotlinter").version("3.7.0")
    id("io.gitlab.arturbosch.detekt").version("1.19.0")
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:2.0.2")
    implementation("io.ktor:ktor-server-netty:1.6.8")
    implementation("io.ktor:ktor-serialization:1.6.8")

    // Logback
    implementation("ch.qos.logback:logback-classic:1.2.11")

    // Testing
    // Kotlin
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.20")

    // Ktor
    testImplementation("io.ktor:ktor-server-tests:2.0.0")
    testImplementation("io.ktor:ktor-server-test-host:2.0.0")
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
