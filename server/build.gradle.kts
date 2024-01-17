dependencies {
    api(project(":api"))

    val springBootVersion = "3.2.1"
    implementation("org.springframework.boot:spring-boot-starter-web:${springBootVersion}")
    implementation("org.springframework.boot:spring-boot-starter-validation:${springBootVersion}")
    implementation("org.springframework.boot:spring-boot-starter-actuator:${springBootVersion}")

    val guavaVersion = "33.0.0-jre"
    implementation("com.google.guava:guava:${guavaVersion}")

    val nettyVersion = "4.1.104.Final"
    implementation("io.netty:netty-all:${nettyVersion}")

    val rapiVersion = "0.15.0"
    annotationProcessor("com.github.therapi:therapi-runtime-javadoc-scribe:${rapiVersion}")
    implementation("com.github.therapi:therapi-runtime-javadoc:${rapiVersion}")

    val reflectionsVersion = "0.10.2"
    implementation("org.reflections:reflections:${reflectionsVersion}")
}
