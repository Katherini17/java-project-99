plugins {
	application
	checkstyle
	jacoco
	id("org.springframework.boot") version "3.5.13"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.github.ben-manes.versions") version "0.53.0"
	id("org.sonarqube") version "7.2.3.7755"
	id("io.freefair.lombok") version "9.2.0"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"
description = "Task management system built with Spring Boot"

val mapstructVersion = "1.6.3"
val lombokMapstructBindingVersion = "0.2.0"
val jacksonNullableVersion = "0.2.9"
val instancioVersion = "6.0.0-RC2"
val datafakerVersion = "2.5.4"
val jsonUnitVersion = "5.1.1"
val springdocVersion = "2.8.16"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	implementation("org.mapstruct:mapstruct:$mapstructVersion")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructBindingVersion")
	implementation("org.openapitools:jackson-databind-nullable:$jacksonNullableVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.instancio:instancio-junit:$instancioVersion")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitVersion")
	implementation("net.datafaker:datafaker:$datafakerVersion")
}

tasks.withType<Test> {
	useJUnitPlatform()

	testLogging {
		events("skipped", "passed", "failed")

		showExceptions = true
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
		showStackTraces = false

		displayGranularity = 2
		showCauses = true
	}

	finalizedBy(tasks.jacocoTestReport)

}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
	}
}

jacoco {
	toolVersion = "0.8.14"
}

application {
	mainClass.set("hexlet.code.AppApplication")
}

sonar {
	properties {
		property("sonar.projectKey", "Katherini17_java-project-99")
		property("sonar.organization", "katherini-17-projects")
	}
}
