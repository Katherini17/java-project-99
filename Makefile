install:
	@./gradlew clean installDist

run-dist:
	@java -jar build/libs/app.jar

run:
	@./gradlew bootRun

build:
	@./gradlew clean build

test:
	@./gradlew test

lint:
	@./gradlew checkstyleMain checkstyleTest

test-report:
	@./gradlew jacocoTestReport

clean:
	@./gradlew clean bootJar

check-updates:
	@./gradlew dependencyUpdates

.PHONY: build