.PHONY: all build clean format lint test

all: clean format lint test build

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
