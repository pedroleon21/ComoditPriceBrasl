import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val statisticsVersion: String by project
val kranglVersion: String by project
val seleniumJavaVersion: String by project
val seleniumDriverVersion: String by project
val jsoupVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.4.31"
}

group = "br.iesb.mobile.kotlin.precoscombustiveis"
version = "1.0"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:$seleniumDriverVersion")
    implementation("org.nield:kotlin-statistics:$statisticsVersion")
    implementation("com.github.holgerbrandl:krangl:$kranglVersion")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}
