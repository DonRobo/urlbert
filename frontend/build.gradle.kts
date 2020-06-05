plugins {
    kotlin("js")
}

group = "at.robbert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
}

kotlin.target.browser { }
