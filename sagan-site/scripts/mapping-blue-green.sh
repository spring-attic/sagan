#!/bin/sh

if [ $# != 4 ] ; then cat << EOM

    This script is used for mapping the blue/green configured environments

    usage: $0 SPACE PATH_TO_CF CURRENT NEXT

    where SPACE is one of (staging|production)
      and PATH_TO_CF is the path to the 'cf' executable
      and CURRENT the name of the current running app (either blue or green)
      and NEXT the name of the app that will be set up to run (the )

EOM
    exit
fi

SPACE=$1
CF=$2
CURRENT=$3
NEXT=$4

echo "==> Starting blue-green mapping script. Current running app is $CURRENT, next running app will be $NEXT"

echo "==> Switching to space $SPACE"
$CF target -s $SPACE || exit

echo "==> Mapping routes to $NEXT"
$CF map-route $NEXT cfapps.io -n sagan-$SPACE || exit

if [ $SPACE == "staging" ]; then

    $CF map-route $NEXT spring.io -n staging

elif [ $SPACE == "production" ]; then

    $CF map-route $NEXT spring.io && $CF unmap-route $CURRENT spring.io
    $CF map-route $NEXT spring.io -n www && $CF unmap-route $CURRENT spring.io -n www

fi

echo "==> Unmapping routes from $CURRENT"
$CF unmap-route $CURRENT cfapps.io -n sagan-$SPACE

if [ $SPACE == "staging" ]; then

    $CF unmap-route $CURRENT spring.io -n staging

elif [ $SPACE == "production" ]; then

    $CF unmap-route $CURRENT spring.io
    $CF unmap-route $CURRENT spring.io -n www

fi
