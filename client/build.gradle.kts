dependencies {
    api(project(":api"))

    val springBootVersion = "3.2.1"
    implementation("org.springframework.boot:spring-boot-starter-web:${springBootVersion}")

    val stormpotVersion = "2.4.2"
    implementation("com.github.chrisvest:stormpot:${stormpotVersion}")
}
