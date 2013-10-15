echo 'creating /tmp/sagan.log'
> /tmp/sagan.log
echo 'writing logs for sagan-blue and sagan-green to /tmp/sagan.log'
cf space production
cf logs sagan-blue >> /tmp/sagan.log &
BLUE=$?
cf logs sagan-green >> /tmp/sagan.log &
GREEN=$?
echo 'tailing /tmp/sagan.log. ^C to stop.'
echo 'logs will continue to be written until pids $BLUE and $GREEN are killed.'
tail -f /tmp/sagan.log
