#!/bin/bash
git pull --rebase
mvn clean compile
mvn sass:watch &
mvn exec:java
