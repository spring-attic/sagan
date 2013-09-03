#!/bin/bash

if [ $# != 1 ] && [ $# != 4 ] ; then cat << EOM

    This script is used for deploying to NON blue/green configured environments

    usage: $0 SPACE PATH_TO_CF EMAIL PASSWORD

    where SPACE is one of (staging|production)
      and (optional) PATH_TO_CF is the path to the 'cf' executable
      and (optional) EMAIL and PASSWORD are your CF credentials

    *** if the optional arguments are not passed in, it assumes 'cf'
        is on your path and you are logged in correctly

    *** passing in one optional argument requires all others as well

EOM
    exit
fi

SPACE=$1
CF=$2
USER=$3
PASS=$4

if [[ -z "$2" ]]; then
    CF=cf
    SPACE=$1
else
    [[ -s "$HOME/.rvm/scripts/rvm" ]] && source "$HOME/.rvm/scripts/rvm" # Load RVM into a shell session *as a function*

    PATH=$PATH:~/.rvm/rubies/ruby-1.9.3-p429/bin/:~/.rvm/bin/

    rvm use 1.9.3@sagan-ops

    $CF target api.run.pivotal.io || exit
    $CF login --email $USER --password $PASS || exit
fi

$CF space $SPACE || exit
$CF push --manifest manifest/$SPACE.yml --reset
