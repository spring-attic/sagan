#!/bin/bash
cd /usr/local/sagan
SPRING_PROFILES_ACTIVE=standalone ./gradlew :sagan-site:bootRun
