#!/bin/bash

# Bring up the Sagan site at http://localhost:8080
$(dirname $0)/../gradlew :sagan-site:run
