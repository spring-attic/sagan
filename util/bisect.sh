#!/bin/bash
BAD=962958c
GOOD=b742859
git bisect start $BAD $GOOD --
git bisect run ./gradlew -x gitProperties clean :sagan-indexer:integTest
git bisect reset
