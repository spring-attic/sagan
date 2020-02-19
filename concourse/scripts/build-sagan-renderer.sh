#!/bin/bash
set -e -u -x
source $(dirname $0)/common.sh
pushd sagan-renderer-repo > /dev/null
./gradlew --console plain :sagan-renderer:build
popd > /dev/null
cp -R sagan-renderer-repo/sagan-renderer/build/* build