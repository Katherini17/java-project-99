plugins {
	application
	checkstyle
	jacoco
	id("org.springframework.boot") version "4.0.3"
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
	implementation("org.springframework.boot:spring-boot-h2console")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	implementation("org.openapitools:jackson-databind-nullable:0.2.9")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.mapstruct:mapstruct:1.6.3")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	constraints {
		implementation("tools.jackson.core:jackson-core:3.1.0") {
			because("fixes security vulnerabilities GHSA-72hv-8253-57qq and CVE-2026-29062")
		}
		implementation("tools.jackson.core:jackson-databind:3.1.0") {
			because("aligns versions with jackson-core to resolve security vulnerabilities")
		}
	}
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