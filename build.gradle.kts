plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com.redwater"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}
repositories {
    mavenCentral()
}

subprojects{
    apply{
        plugin("org.jetbrains.kotlin.jvm")
    }
    repositories {
        mavenCentral()
        maven { url = uri("https://packages.confluent.io/maven/") }
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        implementation(rootProject.libs.ktor.server.kafka)
        implementation(rootProject.libs.ktor.server.core)
        //implementation(rootProject.libs.ktor.serialization.kotlinx.json)
        implementation(rootProject.libs.ktor.server.content.negotiation)

        implementation(rootProject.libs.mongodb.driver.coroutine)
        implementation(rootProject.libs.bson)

        implementation(rootProject.libs.ktor.serialization.gson)

        implementation(rootProject.libs.ktor.server.call.logging)
        implementation(rootProject.libs.call.id)

        implementation(rootProject.libs.ktor.server.host.common)

        implementation(rootProject.libs.ktor.server.status.pages)

        implementation(rootProject.libs.ktor.server.auth)
        implementation(rootProject.libs.ktor.server.auth.jwt)

        implementation(rootProject.libs.ktor.server.netty)
        implementation(rootProject.libs.logback.classic)

        implementation(rootProject.libs.ktor.server.config.yaml)

        implementation(rootProject.libs.retrofit)
        implementation(rootProject.libs.converter.gson)
        implementation(rootProject.libs.logging.interceptor)

        implementation(rootProject.libs.bcrypt)

        testImplementation(rootProject.libs.ktor.server.test.host)
        testImplementation(rootProject.libs.kotlin.test.junit)

        testImplementation(rootProject.libs.junit5)
    }
}

