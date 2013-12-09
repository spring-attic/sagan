#!/bin/bash

if [ $# != 1 ] && [ $# != 4 ] ; then cat << EOM

    This script is used for deploying to blue/green configured environments

    You should build the app before using this script, eg: with 'gradle build'

    usage: $0 SPACE PATH_TO_CF EMAIL PASSWORD

    where SPACE is one of (staging|production)
      and (optional) PATH_TO_CF is the path to the 'gcf' executable
      and (optional) EMAIL and PASSWORD are your CF credentials

    *** if the optional arguments are not passed in, it assumes 'gcf'
        is on your path and you are logged in correctly

    *** passing in one optional argument requires all others as well

EOM
    exit
fi

echo "==> Starting blue-green deploy script"

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
    $CF login -a https://api.run.pivotal.io -u $USER -p $PASS -o spring.io -s $SPACE || exit
fi

echo "==> Checking for running app"
HOST_REGEX=`if [ "$SPACE" == "staging" ]; then echo staging.spring.io; else echo '[^.]spring.io'; fi;`
BLUE_IS_LIVE=`$CF apps | grep $HOST_REGEX | egrep 'sagan-blue' | wc -l`
CURRENT=`if [ $BLUE_IS_LIVE == 1 ]; then echo sagan-blue; else echo sagan-green; fi`
NEXT=`if [ "$CURRENT" == "sagan-green" ]; then echo sagan-blue; else echo sagan-green; fi`

echo "==> $CURRENT is currently live, therefore pushing app to $NEXT"

$CF push $NEXT -m 2G -i 4 -p $SCRIPTDIR/../build/libs/sagan-site.jar -b https://github.com/cloudfoundry/java-buildpack --no-route || $SCRIPTDIR/wait-for-app-to-start.sh $NEXT 100 $CF || exit

echo "==> Successfully pushed app to $NEXT; now mapping routes to $NEXT and unmapping routes from $CURRENT"

$SCRIPTDIR/mapping-blue-green.sh $SPACE $CF $CURRENT $NEXT

echo "==> Deployment complete."
