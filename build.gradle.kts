import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
}

repositories {
    mavenCentral()
}

dependencies {
    kotlin("script-runtime")
    implementation(kotlin("reflect"))

    implementation("org.apache.commons:commons-imaging:1.0-alpha2")

    val junitJupiterVersion = "5.6.0"
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")

    testImplementation("com.natpryce:hamkrest:1.8.0.1")
    testImplementation("io.mockk:mockk:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.io.path.ExperimentalPathApi"
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/quincy/kexif")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("kexif") {
            from(components["java"])
            pom {
                name.set("kexif")
                description.set("An image metadata reader/writer in Kotlin")
                url.set("https://github.com/quincy/kexif")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.opensource.org/licenses/mit-license.php")
                        distribution.set("repo")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/quincy/kexif.git")
                    developerConnection.set("scm:git:ssh://github.com/quincy/kexif.git")
                    url.set("https://github.com/quincy/kexif/")
                }

                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/quincy/kexif/issues")
                }
            }
        }
    }
}

bintray {
    user = project.findProperty("bintrayUser").toString()
    key = project.findProperty("bintrayKey").toString()
    pkg.apply {
        repo = "kexif-jvm"
        name = "kexif"
        description = "Image metadata reader/writer in Kotlin"
        setLicenses("MIT")
        vcsUrl = "https://github.com/quincy/kexif.git"
    }
}