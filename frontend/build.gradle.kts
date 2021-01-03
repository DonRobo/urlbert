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
    maven(url = "https://kotlin.bintray.com/kotlinx/")
}

dependencies {
    val wrapperKotlinVersion = "pre.129-kotlin-1.4.20"

    implementation(kotlin("stdlib-js"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.1")

    implementation(project(":shared"))

    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.2")
    implementation("org.jetbrains:kotlin-react:17.0.0-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-react-dom:17.0.0-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-react-router-dom:5.2.0-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-react-redux:7.2.1-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-styled:5.2.0-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-extensions:1.0.1-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-css-js:1.0.0-$wrapperKotlinVersion")
}

tasks.withType<Kotlin2JsCompile>().configureEach {
    kotlinOptions.suppressWarnings = true
    kotlinOptions.sourceMap = true
    kotlinOptions.sourceMapEmbedSources = "always"
}

kotlin {
    js(LEGACY) {
        useCommonJs()
        browser {
        }
        binaries.executable()
    }
}
