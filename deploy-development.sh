#!/bin/bash

if [ $# != 3 ]; then cat << EOM

    usage: $0 PATH_TO_CF EMAIL PASSWORD

    where PATH_TO_CF is the path to the 'cf' executable
      and EMAIL and PASSWORD are your CF credentials

EOM
    exit
fi

CF=$1
USER=$2
PASS=$3

[[ -s "$HOME/.rvm/scripts/rvm" ]] && source "$HOME/.rvm/scripts/rvm" # Load RVM into a shell session *as a function*

PATH=$PATH:~/.rvm/rubies/ruby-1.9.3-p429/bin/:~/.rvm/bin/

rvm use 1.9.3@sagan-ops

$CF target api.run.pivotal.io || exit
$CF login --email $USER --password $PASS || exit
$CF space development || exit
$CF push --manifest manifest-development.yml --reset
