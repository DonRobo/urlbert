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
    val wrapperKotlinVersion = "pre.107-kotlin-1.3.72"
    val serializationVersion = "0.20.0"

    implementation(kotlin("stdlib-js"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.7")

    implementation(project(":shared"))

    implementation("org.jetbrains.kotlinx:kotlinx-html:0.6.12")
    implementation("org.jetbrains:kotlin-react:16.13.1-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-react-dom:16.13.1-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-react-router-dom:5.1.2-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-react-redux:5.0.7-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-styled:1.0.0-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-extensions:1.0.1-$wrapperKotlinVersion")
    implementation("org.jetbrains:kotlin-css-js:1.0.0-$wrapperKotlinVersion")
//    implementation(npm("react", "16.13.1"))
//    implementation(npm("react-dom", "16.13.1"))
//    implementation(npm("react-is", "16.13.1"))
//    implementation(npm("inline-style-prefixer", "5.1.2"))
//    implementation(npm("styled-components", "4.3.2"))
}

tasks.withType<Kotlin2JsCompile>().configureEach {
    kotlinOptions.suppressWarnings = true
    kotlinOptions.sourceMap = true
    kotlinOptions.sourceMapEmbedSources = "always"
}

kotlin {
    target {
        useCommonJs()
        browser {
        }
    }
}
