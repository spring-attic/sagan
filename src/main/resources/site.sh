#!/bin/sh

java -Xmx1260m -XX:MaxPermSize=512m -classpath "." org.springframework.launcher.JarLauncher $*