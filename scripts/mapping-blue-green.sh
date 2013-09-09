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

# Map new app
$CF map --app $NEXT --host sagan-$SPACE --domain cfapps.io || exit
# Unmap old app, leaving it running for rollback
$CF unmap --app $CURRENT --url sagan-$SPACE.cfapps.io

if [ $SPACE == "production" ]; then

    $CF map   --app $NEXT    --domain               spring.io
    $CF unmap --app $CURRENT --url                 .spring.io &
#    $CF map   --app $NEXT    --host www    --domain spring.io
#    $CF unmap --app $CURRENT --url              www.spring.io &
#
#    $CF map   --app $NEXT    --domain               springsource.org
#    $CF unmap --app $CURRENT --url                 .springsource.org &
#    $CF map   --app $NEXT    --domain               springframework.org
#    $CF unmap --app $CURRENT --url                 .springframework.org &
#    $CF map   --app $NEXT    --domain               springsource.com
#    $CF unmap --app $CURRENT --url                 .springsource.com &
#    $CF map   --app $NEXT    --domain               interface21.com
#    $CF unmap --app $CURRENT --url                 .interface21.com &
#    $CF map   --app $NEXT    --domain               springframework.io
#    $CF unmap --app $CURRENT --url                 .springframework.io &
#    $CF map   --app $NEXT    --domain               springsource.io
#    $CF unmap --app $CURRENT --url                 .springsource.io &
#
#    $CF map   --app $NEXT    --host static --domain springframework.org
#    $CF unmap --app $CURRENT --url           static.springframework.org &
#    $CF map   --app $NEXT    --host static --domain springsource.org
#    $CF unmap --app $CURRENT --url           static.springsource.org &
#
#    $CF map   --app $NEXT    --host blog   --domain springsource.org
#    $CF unmap --app $CURRENT --url             blog.springsource.org &
#    $CF map   --app $NEXT    --host blog   --domain springsource.com
#    $CF unmap --app $CURRENT --url             blog.springsource.com &
#    $CF map   --app $NEXT    --host blog   --domain interface21.com
#    $CF unmap --app $CURRENT --url             blog.interface21.com &
#
#    $CF map   --app $NEXT    --host www    --domain interface21.com
#    $CF unmap --app $CURRENT --url              www.interface21.com &
#    $CF map   --app $NEXT    --host www    --domain springsource.org
#    $CF unmap --app $CURRENT --url              www.springsource.org &
#    $CF map   --app $NEXT    --host www    --domain springframework.org
#    $CF unmap --app $CURRENT --url              www.springframework.org &
#    $CF map   --app $NEXT    --host www    --domain springsource.com
#    $CF unmap --app $CURRENT --url              www.springsource.com &
#    $CF map   --app $NEXT    --host www    --domain springframework.io
#    $CF unmap --app $CURRENT --url              www.springframework.io &
#    $CF map   --app $NEXT    --host www    --domain springsource.io
#    $CF unmap --app $CURRENT --url              www.springsource.io
#
fi;

