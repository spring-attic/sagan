#!/bin/sh

if [ $# != 2 ]; then cat << EOM
    usage: $0 DB_URL_SOURCE DB_URL_DESTINATION
EOM
    exit
fi

echo "Creating dump from $1"
pg_dump -a -O $1  > /tmp/dump.sql

echo "Replicating into $2"
psql $2 -c "truncate memberprofile restart identity cascade"
psql $2 -c "truncate databasechangelog"
psql $2 -c "truncate databasechangeloglock"
psql $2 < /tmp/dump.sql
