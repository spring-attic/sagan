#!/bin/bash

if [ $# != 1 ] && [ $# != 4 ] ; then cat << EOM

    This script is used for deploying to blue/green configured environments

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

echo "Starting blue-green deploy script"

SPACE=$1
CF=$2
USER=$3
PASS=$4

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

echo "Checking for running app"
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

$CF push --manifest manifest/$SPACE.yml --name $NEXT --reset --start || exit

# Map new app
$CF map --app $NEXT --host sagan-$SPACE --domain cfapps.io || exit
# Unmap old app, leaving it running for rollback
$CF unmap --app $CURRENT --url sagan-$SPACE.cfapps.io

if [ $SPACE == "production" ]; then

    $CF map   --app $NEXT    --domain               spring.io
    $CF unmap --app $CURRENT --url                 .spring.io &
    $CF map   --app $NEXT    --host www    --domain spring.io
    $CF unmap --app $CURRENT --url              www.spring.io &

    $CF map   --app $NEXT    --domain               springsource.org
    $CF unmap --app $CURRENT --url                 .springsource.org &
    $CF map   --app $NEXT    --domain               springframework.org
    $CF unmap --app $CURRENT --url                 .springframework.org &
    $CF map   --app $NEXT    --domain               springsource.com
    $CF unmap --app $CURRENT --url                 .springsource.com &
    $CF map   --app $NEXT    --domain               interface21.com
    $CF unmap --app $CURRENT --url                 .interface21.com &
    $CF map   --app $NEXT    --domain               springframework.io
    $CF unmap --app $CURRENT --url                 .springframework.io &
    $CF map   --app $NEXT    --domain               springsource.io
    $CF unmap --app $CURRENT --url                 .springsource.io &

    $CF map   --app $NEXT    --host static --domain springframework.org
    $CF unmap --app $CURRENT --url           static.springframework.org &
    $CF map   --app $NEXT    --host static --domain springsource.org
    $CF unmap --app $CURRENT --url           static.springsource.org &

    $CF map   --app $NEXT    --host blog   --domain springsource.org
    $CF unmap --app $CURRENT --url             blog.springsource.org &
    $CF map   --app $NEXT    --host blog   --domain springsource.com
    $CF unmap --app $CURRENT --url             blog.springsource.com &
    $CF map   --app $NEXT    --host blog   --domain interface21.com
    $CF unmap --app $CURRENT --url             blog.interface21.com &

    $CF map   --app $NEXT    --host www    --domain interface21.com
    $CF unmap --app $CURRENT --url              www.interface21.com &
    $CF map   --app $NEXT    --host www    --domain springsource.org
    $CF unmap --app $CURRENT --url              www.springsource.org &
    $CF map   --app $NEXT    --host www    --domain springframework.org
    $CF unmap --app $CURRENT --url              www.springframework.org &
    $CF map   --app $NEXT    --host www    --domain springsource.com
    $CF unmap --app $CURRENT --url              www.springsource.com &
    $CF map   --app $NEXT    --host www    --domain springframework.io
    $CF unmap --app $CURRENT --url              www.springframework.io &
    $CF map   --app $NEXT    --host www    --domain springsource.io
    $CF unmap --app $CURRENT --url              www.springsource.io

fi;

$CF push --manifest manifest/$SPACE.yml --name sagan-indexer --host sagan-indexer-$SPACE --reset --start
