#!/bin/bash

if [ $# != 1 ] && [ $# != 4 ] ; then cat << EOM

    This script is used for deploying to single app configured environments (i.e. NON blue/green)

    You should build the app before using this script, eg: 'mvn clean package'

    usage: $0 SPACE PATH_TO_CF EMAIL PASSWORD

    where SPACE is one of (staging|production)
      and (optional) PATH_TO_CF is the path to the 'cf' executable
      and (optional) EMAIL and PASSWORD are your CF credentials

    *** if the optional arguments are not passed in, it assumes 'cf'
        is on your path and you are logged in correctly

    *** passing in one optional argument requires all others as well

EOM
    exit 1
fi

echo "==> Starting deploy script"

SPACE=$1
CF=$2
USER=$3
PASS=$4
SCRIPTDIR=$(dirname $0)

if [[ -z "$2" ]]; then
    CF=gcf
    SPACE=$1

    echo "==> Targeting space $SPACE"
    $CF target -s $SPACE
else
    echo "==> Validating $CF executable"
    if [[ ! -f $CF ]]; then echo "$CF does not exist"; exit 100; fi
    if [[ ! -x $CF ]]; then echo "$CF is not executable"; exit 101; fi

    echo "==> Logging in to CF"
    $CF login -a https://api.run.pivotal.io -u $USER -p $PASS -o spring.io -s $SPACE || exit 102
fi

echo "==> Pushing sagan-indexer"
$CF push sagan-indexer -m 2G -i 1 -p $SCRIPTDIR/../build/libs/sagan-indexer.jar -b https://github.com/cloudfoundry/java-buildpack --no-route || exit 103

echo "==> Mapping routes to sagan-indexer"
$CF create-route $SPACE cfapps.io -n sagan-indexer-$SPACE || exit 104
$CF map-route sagan-indexer cfapps.io -n sagan-indexer-$SPACE|| exit 104

echo "==> Deployment complete."
