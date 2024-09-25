plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "com.redwater.triagehub"
include("user")
include("shared")
include("api-gateway")
include("organisation")
include("notification")
