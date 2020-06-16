import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("js")
}

group = "at.robbert"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-dev")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
    maven("https://dl.bintray.com/kotlin/kotlinx")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
}

tasks.withType<Kotlin2JsCompile>().configureEach {
    kotlinOptions.suppressWarnings = true
    kotlinOptions.sourceMap = false
    kotlinOptions.sourceMapEmbedSources = "never"
}

kotlin {
    target {
        useCommonJs()
        browser {
        }
    }
}
