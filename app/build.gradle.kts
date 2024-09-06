plugins {
    application
    checkstyle
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.10"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass = "hexlet.code.App"
}

checkstyle {
    toolVersion = "10.12.4"
}

dependencies {

    // jupiter
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // javalin
    implementation("io.javalin:javalin:6.3.0")

    // slf4j
    implementation("org.slf4j:slf4j-simple:2.0.16")

}

tasks.test {
    useJUnitPlatform()
}