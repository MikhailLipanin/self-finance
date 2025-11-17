.PHONY: help compile clean run package install test

MAVEN = mvn
JAVA = java
JAR_FILE = target/self-finance-1.0-SNAPSHOT.jar
MAIN_CLASS = com.selffinance.Main

compile:
	$(MAVEN) clean compile

clean:
	$(MAVEN) clean

run:
	$(MAVEN) exec:java -Dexec.mainClass="$(MAIN_CLASS)"

run-jar: package
	$(JAVA) -jar $(JAR_FILE)

package:
	$(MAVEN) clean package

install:
	$(MAVEN) clean install

# Валидация pom.xml
validate:
	$(MAVEN) validate

all: clean package
