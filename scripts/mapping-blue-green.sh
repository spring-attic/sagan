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

    ($CF map $NEXT staging spring.io && $CF unmap staging.spring.io $CURRENT)

elif [ $SPACE == "production" ]; then

    ($CF map $NEXT spring.io && $CF unmap .spring.io $CURRENT) &
    ($CF map $NEXT static springsource.org && $CF unmap static.springsource.org $CURRENT) &
    ($CF map $NEXT www springsource.org && $CF unmap www.springsource.org $CURRENT) &
    ($CF map $NEXT forum springsource.org && $CF unmap forum.springsource.org $CURRENT) &
    ($CF map $NEXT blog springsource.org && $CF unmap blog.springsource.org $CURRENT) &
    ($CF map $NEXT springsource.org && $CF unmap .springsource.org $CURRENT) &
    ($CF map $NEXT springframework.org && $CF unmap .springframework.org $CURRENT) &
    ($CF map $NEXT www springframework.org && $CF unmap www.springframework.org $CURRENT) &
    ($CF map $NEXT static springframework.org && $CF unmap static.springframework.org $CURRENT) &
    ($CF map $NEXT blog springsource.com && $CF unmap blog.springsource.com $CURRENT) &
    ($CF map $NEXT springsource.com && $CF unmap .springsource.com $CURRENT) &
    ($CF map $NEXT forum springframework.org && $CF unmap forum.springframework.org $CURRENT) &
    ($CF map $NEXT blog interface21.com && $CF unmap blog.interface21.com $CURRENT) &
    ($CF map $NEXT www springsource.com && $CF unmap www.springsource.com $CURRENT) &
    ($CF map $NEXT interface21.com && $CF unmap .interface21.com $CURRENT) &
    ($CF map $NEXT www interface21.com && $CF unmap www.interface21.com $CURRENT) &
    ($CF map $NEXT www spring.io && $CF unmap www.spring.io $CURRENT) &
    ($CF map $NEXT springframework.io && $CF unmap .springframework.io $CURRENT) &
    ($CF map $NEXT springsource.io && $CF unmap .springsource.io $CURRENT) &
    ($CF map $NEXT www springframework.io && $CF unmap www.springframework.io $CURRENT) &
    ($CF map $NEXT www springsource.io && $CF unmap www.springsource.io $CURRENT) &

    wait
fi;

