install:
	@./gradlew clean install

run:
	@./gradlew bootRun

build:
	@./gradlew clean build

test:
	@./gradlew cleanTest test

lint:
	@./gradlew checkstyleMain checkstyleTest

test-report:
	@./gradlew jacocoTestReport

clean:
	@./gradlew clean

check-updates:
	@./gradlew dependencyUpdates

.PHONY: build test clean run lint check-updates test-report install
