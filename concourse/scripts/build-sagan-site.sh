#!/bin/bash
set -e -u -x
source $(dirname $0)/common.sh
chown gradle:gradle -R ./sagan-site-repo
pushd sagan-site-repo > /dev/null
su gradle -c "./gradlew --console plain :sagan-site:build"
popd > /dev/null
cp -R sagan-site-repo/sagan-site/build/* build