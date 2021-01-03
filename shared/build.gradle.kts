plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "at.robbert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js {
        browser {}
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
                implementation("org.springframework.data:spring-data-commons:2.4.2")
                implementation("org.apache.commons:commons-text:1.8")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.1")
            }
        }
    }
}
