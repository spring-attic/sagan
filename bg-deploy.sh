#!/bin/bash

if [ $# != 4 ]; then cat << EOM

    usage: $0 PATH_TO_CF EMAIL PASSWORD SPACE

    where PATH_TO_CF is the path to the 'cf' executable
      and EMAIL and PASSWORD are your CF credentials
      and SPACE is one of (staging|production)

EOM
    exit
fi

CF=$1
USER=$2
PASS=$3
SPACE=$4

[[ -s "$HOME/.rvm/scripts/rvm" ]] && source "$HOME/.rvm/scripts/rvm" # Load RVM into a shell session *as a function*

PATH=$PATH:~/.rvm/rubies/ruby-1.9.3-p429/bin/:~/.rvm/bin/

rvm use 1.9.3@sagan-ops

$CF target api.run.pivotal.io || exit
$CF login --email $USER --password $PASS || exit
$CF space $SPACE || exit

# check that we don't have >1 or 0 apps running. We might make a case for 0 and deploy sagan-blue.
CHECK=`$CF apps --url sagan-$SPACE.cfapps.io | grep -E 'green|blue' | wc -l`

if [ $CHECK != 1 ];
    then echo "There must be exactly one CF green or blue app running. Exiting.";
    exit 1;
fi

CURRENT=`$CF apps --url sagan-$SPACE.cfapps.io | grep -E 'green|blue'`

echo App currently running is $CURRENT.

NEXT=`if [ "$CURRENT" == "sagan-green" ]; then echo sagan-blue; else echo sagan-green; fi`

echo Next app to be deployed will be $NEXT.

$CF push --manifest manifest-$SPACE.yml --name $NEXT --reset --start

# Map new app
$CF map --app $NEXT --host sagan-$SPACE --domain cfapps.io
# Unmap old app, leaving it running for rollback
$CF unmap --app $CURRENT --url sagan-$SPACE.cfapps.io
