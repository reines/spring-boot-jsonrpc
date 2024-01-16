subprojects {
    apply {
        plugin("java-library")
        plugin("maven-publish")
        plugin("signing")
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/reines/spring-boot-jsonrpc")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
        val publication = publications.create<MavenPublication>(project.name) {
            groupId = "com.furnaghan.spring.jsonrpc"
            artifactId = project.name
            version = "${project.version}"

            from(project.components["java"])
        }

        configure<SigningExtension> {
            val signingKey = System.getenv("SIGNING_KEY")
            val signingPassword = System.getenv("SIGNING_PASSWORD")
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publication)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
