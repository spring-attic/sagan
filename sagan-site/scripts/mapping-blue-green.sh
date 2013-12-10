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
NEXT_COLOR=`echo $NEXT | sed 's/sagan-//'`

echo "==> Starting blue-green mapping script. Current running app is $CURRENT, next running app will be $NEXT"

echo "==> Switching to space $SPACE"
$CF target -s $SPACE || exit

echo "==> Mapping routes to $NEXT"
$CF create-route $SPACE cfapps.io -n sagan-$SPACE-$NEXT_COLOR || exit
$CF map-route $NEXT cfapps.io -n sagan-$SPACE-$NEXT_COLOR || exit

$CF create-route $SPACE cfapps.io -n sagan-$SPACE || exit
$CF map-route $NEXT cfapps.io -n sagan-$SPACE || exit
$CF unmap-route $CURRENT cfapps.io -n sagan-$SPACE

if [ $SPACE == "staging" ]; then

    $CF create-route $SPACE spring.io -n staging || exit
    $CF map-route $NEXT spring.io -n staging || exit
    $CF unmap-route $CURRENT spring.io -n staging

elif [ $SPACE == "production" ]; then

    $CF create-route $SPACE spring.io || exit
    $CF map-route $NEXT spring.io || exit
    $CF unmap-route $CURRENT spring.io

    $CF create-route $SPACE spring.io -n www || exit
    $CF map-route $NEXT spring.io -n www || exit
    $CF unmap-route $CURRENT spring.io -n www

fi

echo "==> Mapping routes to $NEXT complete."
