#!/bin/bash
BAD=962958c
GOOD=b742859
git bisect start $BAD $GOOD --
git bisect run ./gradlew clean build -x gitProperties
git bisect reset
