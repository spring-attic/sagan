#!/bin/sh

if [ $# != 3 ] ; then cat << EOM
    This script polls 'cf health' repeteadly to find out if an app is running.

    It will stop when an app is running or if a maximun number of retries is reached.

    usage: $0 APP_NAME MAX_RETRIES PATH_TO_CF
EOM
    exit
fi

APP_NAME=$1
MAX_RETRIES=$2
CF=$3

echo "Polling cf health for $APP_NAME"

RETRY=0
while [ $RETRY -lt $MAX_RETRIES ]; do
    HEALTH=`$CF health $APP_NAME`

    if [[ $HEALTH =~ running|[1-9][0-9]?% ]]; then
        echo "$APP_NAME is running!"
        exit 0
    fi

    if [[ $HEALTH == "staging failed" ]]; then
        echo "$APP_NAME failed to stage. Needs to be re-deployed"
        exit 1
    fi

    let RETRY=RETRY+1

    echo "retry $RETRY of $MAX_RETRIES where '$APP_NAME' health is '$HEALTH'"
done

echo "Polling of $APP_NAME reached the maximum number of retries"
exit 1
