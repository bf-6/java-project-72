import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    checkstyle
    jacoco
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

    // assertj // jupiter
    testImplementation("org.assertj:assertj-core:3.27.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // javalin // jte
    implementation("io.javalin:javalin:6.3.0")
    implementation("io.javalin:javalin-bundle:6.3.0")
    implementation("io.javalin:javalin-rendering:6.3.0")
    implementation("gg.jte:jte:3.1.9")
    // slf4j
    implementation("org.slf4j:slf4j-simple:2.0.16")
    // HikariCP // h2 database
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.h2database:h2:2.2.224")
    // postgresql
    implementation("org.postgresql:postgresql:42.7.3")
    // https://mvnrepository.com/artifact/org.apache.httpcomponents.client5/httpclient5
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.1")
    // unirest-java
    implementation("com.mashape.unirest:unirest-java:1.4.9")
    // mockwebserver
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    // jsoup
    implementation("org.jsoup:jsoup:1.18.3")



}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    // https://technology.lastminute.com/junit5-kotlin-and-gradle-dsl/
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        // showStackTraces = true
        // showCauses = true
        showStandardStreams = true
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}