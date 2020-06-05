plugins {
    kotlin("multiplatform")
}

group = "at.robbert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
    }
}
