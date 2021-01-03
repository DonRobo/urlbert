import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm")
    kotlin("plugin.spring")
}

group = "at.robbert"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(project(":shared", "jvmRuntimeElements"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
    implementation("io.projectreactor:reactor-tools:3.4.1")

    implementation("com.maxmind.geoip2:geoip2:2.13.1")

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.flywaydb:flyway-core")
    implementation("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
tasks.withType<BootJar> {
    destinationDirectory.set(project.rootDir)
    archiveFileName.set("urlbert.jar")
}
