plugins {
    kotlin("multiplatform").version("1.4.21").apply(false)
    kotlin("plugin.serialization").version("1.4.21").apply(false)
    kotlin("plugin.spring") version "1.4.21" apply false
}

allprojects {
    version = "1.0-SNAPSHOT"
}

group = "at.robbert"

repositories {
    mavenCentral()
}
