plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
}

group = "com.redwater"
version = "0.0.1"

application{
    mainClass.set("com.example.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}
dependencies {
    //implementation(project(":shared"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}