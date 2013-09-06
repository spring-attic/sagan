#!/bin/sh

if [ $# != 1 ] ; then cat << EOM

    This script is used for checking which blue/green app is currently live

    usage: $0 SPACE

    where SPACE is one of (staging|production)

EOM
    exit
fi

SPACE=$1

echo "switching to space $SPACE"
cf space $SPACE || exit

CURRENT=`cf apps --url sagan-$SPACE.cfapps.io | grep -E 'green|blue'`

echo $CURRENT
