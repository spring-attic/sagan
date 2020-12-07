#!/bin/bash
set -e -u -x
source $(dirname $0)/common.sh
repository=$(pwd)/distribution-repository
mkdir -p ${repository}
chown gradle:gradle -R ./sagan-site-repo ${repository}
pushd sagan-site-repo > /dev/null
su gradle -c "./gradlew --console plain -Dorg.gradle.internal.launcher.welcomeMessageEnabled=false --no-daemon --max-workers=4 -PdeploymentRepository=${repository} :sagan-site:build :sagan-site:publishAllPublicationsToDeploymentRepository"
popd > /dev/null
cp -R sagan-site-repo/sagan-site/build/* build