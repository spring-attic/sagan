#!/bin/bash
git pull --rebase
mvn clean compile
classpath=$(mvn exec:exec -Dexec.executable="echo" -Dexec.args="CLASSPATH %classpath" | grep "^CLASSPATH" | cut -d" " -f2)
mvn sass:watch &
mvn exec:exec -Dexec.executable="java" -Dexec.args="-classpath src/main/resources:$classpath org.springframework.site.web.configuration.ApplicationConfiguration"
