import com.rohanprabhu.gradle.plugins.kdjooq.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.util.*

plugins {
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.rohanprabhu.kotlin-dsl-jooq") version "0.4.6"
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

    implementation("de.gofabian:spring-boot-data-r2dbc-jooq:0.3.0")
    jooqGeneratorRuntime("org.postgresql:postgresql")
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

val props = Properties()
file("${projectDir.absolutePath}/src/main/resources/application.properties")
    .bufferedReader().use {
        props.load(it)
    }

jooqGenerator {
    jooqEdition = JooqEdition.OpenSource
    jooqVersion = "3.13.4"

    configuration("primary", project.sourceSets.getByName("main")) {
        databaseSources {
            +"${projectDir.absolutePath}/src/main/resources/db/migration"
        }
        configuration = jooqCodegenConfiguration {
            databaseSources {
                +"${project.projectDir}/src/main/resources/db/migration"
            }
            jdbc {
                driver = props["spring.datasource.driver-class-name"] as String
                username = props["spring.datasource.username"] as String
                password = props["spring.datasource.password"] as String
                url = props["spring.datasource.url"] as String
            }
            generator {
                name = "org.jooq.codegen.DefaultGenerator"

                target {
                    packageName = "at.robbert.backend.jooq"
                    directory = "${buildDir.absolutePath}/generated/jooq/primary"
                }

                database {
                    name = "org.jooq.meta.postgres.PostgresDatabase"
                    inputSchema = "public"
                }
                generate {
                    withVarargSetters(false)
                }
            }
        }
    }
}
