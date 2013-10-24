#!/bin/sh

if [ $# != 3 ] ; then cat << EOM

    This script is used for updating an environment variable for blue/green configurations

    The script will set the variable on the currently inactive app, and restart it.

    Users of this script are advised to manually test and check this update and then push or remap.

    After remapping, users should run this script again to ensure consistency in the environment
    variables across both blue and green apps.

    usage: $0 SPACE NAME VALUE

    where SPACE is one of (staging|production)
      and NAME is the environment variable to be updated
      and VALUE is the new value for the environment variable

    Pre-requisites: CF gem is installed and you are logged in

EOM
    exit
fi

SPACE=$1
NAME=$2
VALUE=$3

echo "switching to space $SPACE"
cf space $SPACE || exit

CURRENT=`cf apps --url sagan-$SPACE.cfapps.io | grep -E 'green|blue'`

INACTIVE=`if [ "$CURRENT" == "sagan-green" ]; then echo sagan-blue; else echo sagan-green; fi`

echo "Environment variable will be updated on: $INACTIVE."

cf set-env $INACTIVE $NAME $VALUE || exit

echo "The environment has been updated."

cf env $INACTIVE