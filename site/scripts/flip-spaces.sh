#!/bin/bash

if [ $# != 3 ]; then cat << EOM

    usage: $0 DOMAIN TARGET_SPACE TARGET_APP

EOM
    exit
fi

DOMAIN=$1
TARGET_SPACE=$2
TARGET_APP=$3

cf space $TARGET_SPACE
(echo y | cf delete-route .$DOMAIN; cf map $TARGET_APP $DOMAIN) &
(echo y | cf delete-route www.$DOMAIN; cf map $TARGET_APP www $DOMAIN) &
wait
