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
    exit
fi

echo "Starting simple deploy script"

SPACE=$1
CF=$2
USER=$3
PASS=$4
SCRIPTDIR=$(dirname $0)

if [[ -z "$2" ]]; then
    CF=cf
    SPACE=$1
else
    echo "loading RVM"
    [[ -s "$HOME/.rvm/scripts/rvm" ]] && source "$HOME/.rvm/scripts/rvm" # Load RVM into a shell session *as a function*

    PATH=$PATH:~/.rvm/rubies/ruby-1.9.3-p429/bin/:~/.rvm/bin/

    rvm use 1.9.3@sagan-ops

    echo "logging in to CF"
    $CF target api.run.pivotal.io || exit
    $CF login --email $USER --password $PASS || exit
fi

echo "switching to space $SPACE"
$CF space $SPACE || exit

echo "pushing indexer to CF"
$CF push --manifest $SCRIPTDIR/../manifest/$SPACE.yml --name sagan-indexer --host $SPACE-sagan-indexer --reset --start || $SCRIPTDIR/wait-for-app-to-start.sh sagan-indexer 100 $CF
