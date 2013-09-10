#!/bin/sh

if [ $# != 2 ] ; then cat << EOM

    This script polls 'cf health' repeteadly to find out if an app is running.

    It will stop when an app is running or if a maximun number of retries is reached.

    usage: $0 APP_NAME MAX_RETRIES
EOM
    exit
fi


APP_NAME=$1
MAX_RETRIES=$2

echo "Polling cf health for $APP_NAME"

RETRY=0
while [ $RETRY -lt $MAX_RETRIES ]; do
    HEALTH=`cf health $APP_NAME`
    if [[ $HEALTH != "stopped" && $HEALTH != "0%" ]]; then
        echo "$APP_NAME is running!"
        exit 0
    fi
    let RETRY=RETRY+1
    echo "retry $RETRY of $MAX_RETRIES where '$APP_NAME' health is '$HEALTH'"
done
exit 1
