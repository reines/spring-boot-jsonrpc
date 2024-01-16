rootProject.name = "spring-boot-jsonrpc"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":api")
include(":server")
include(":client")
