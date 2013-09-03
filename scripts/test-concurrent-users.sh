#!/bin/bash

if [ $# != 3 ]; then cat << EOM

    Welcome to the concurrent testing script

    This script will hit all the main endpoints of the Spring.io website with a number of concurrent users.

    REQUIREMENTS: Apache Bench
    On OSX Lion+ it is recommended that you use the latest version of Apache Bench to avoid "Send request failed!" errors

    ------------------------------------------------------

    usage: $0 HOST CONCURRENT_USERS REQUESTS_PER_TEST

    where HOST uses the format [http[s]://]hostname[:port]

EOM
    exit
fi

HOST=$1
CONCURRENT_USERS=$2
REQUESTS_PER_TEST=$3

function hit_path {
    echo
    echo "*********************************************************************"
    echo "  Benchmarking: $HOST/$1 with $CONCURRENT_USERS users and $REQUESTS_PER_TEST requests"
    echo "*********************************************************************"
    echo
    ab -c $CONCURRENT_USERS -n $REQUESTS_PER_TEST "$HOST/$1"
}

hit_path ""
hit_path "about"
hit_path "blog"
hit_path "blog/2013/08/14/spring-xd-1-0-0-m2-released"
hit_path "blog/category/engineering"
hit_path "blog/category/releases"
hit_path "blog/category/news"
hit_path "docs"
hit_path "guides"
hit_path "guides/gs/rest-service"
hit_path "guides/gs/securing-web/"
hit_path "guides/tutorials/rest"
hit_path "guides/tutorials/rest/1"
hit_path "jobs"
hit_path "logos"
hit_path "projects"
hit_path "projects/attic"
hit_path "search?q=ApplicationContext&page=12"
hit_path "search?q=blog"
hit_path "services"
hit_path "team"
hit_path "team/cbeams"
hit_path "tools"
hit_path "tools/eclipse"
hit_path "tools/ggts"
hit_path "tools/ggts/all"
hit_path "tools/sts"
hit_path "tools/sts/all"
