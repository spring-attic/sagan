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

echo "Starting blue-green mapping script. Current running app is $CURRENT, next running app will be $NEXT"

echo "switching to space $SPACE"
$CF space $SPACE || exit

($CF map $NEXT sagan-$SPACE cfapps.io && $CF unmap sagan-$SPACE.cfapps.io $CURRENT) || exit

if [ $SPACE == "staging" ]; then

    $CF map $NEXT staging spring.io && $CF unmap staging.spring.io $CURRENT

elif [ $SPACE == "production" ]; then

    ($CF map $NEXT spring.io && $CF unmap .spring.io $CURRENT) &
    ($CF map $NEXT www spring.io && $CF unmap www.spring.io $CURRENT) &

    wait
fi

