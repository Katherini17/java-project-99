install:
	@./gradlew clean installDist

run-dist:
	@./app/build/install/app/bin/app

run:
	@./gradlew run

build:
	@./gradlew clean build

test:
	@./gradlew test

lint:
	@./gradlew checkstyleMain checkstyleTest

test-report:
	@./gradlew jacocoTestReport

clean:
	@./gradlew clean

.PHONY: build