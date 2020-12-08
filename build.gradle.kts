import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
}

group = "com.quakbo"
version = "1.0-SNAPSHOT"

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
