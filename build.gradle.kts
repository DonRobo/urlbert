plugins {
    kotlin("multiplatform").version("1.3.72").apply(false)
    kotlin("plugin.serialization").version("1.3.72").apply(false)
    kotlin("plugin.spring") version "1.3.72" apply false
}

allprojects {
    version = "1.0-SNAPSHOT"
}

group = "at.robbert"

repositories {
    mavenCentral()
}
