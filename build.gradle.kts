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
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	implementation("org.mapstruct:mapstruct:1.6.3")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	implementation("org.openapitools:jackson-databind-nullable:0.2.9")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.instancio:instancio-junit:6.0.0-RC2")
	implementation("net.datafaker:datafaker:2.5.4")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:5.1.1")

}

tasks.withType<Test> {
	useJUnitPlatform()
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

tasks.test {
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