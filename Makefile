.PHONY: all build clean format lint report test upload-coverage

all: clean lint test build

build:
	./gradlew build

clean:
	./gradlew clean

format:
	./gradlew formatKotlin

lint:
	./gradlew lintKotlin detekt

test:
	./gradlew test

