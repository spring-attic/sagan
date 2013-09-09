#! /bin/sh

# Clear and re-configure the search index.

if [ $# != 2 ]; then cat << EOM

    usage: $0 ENDPOINT INDEX

    where ENDPOINT is the path to the qbox.io endpoint (or http://localhost:9200)
      and INDEX is the name of the endpoint, e.g. 'sagan-production'

EOM
    exit
fi


ENDPOINT=$1
INDEX=$2

echo "\nDelete the existing index."
curl -XDELETE "$ENDPOINT/$INDEX"
sleep 1

echo "\nCreate the index again."
curl -X PUT $ENDPOINT/$INDEX -d '{ "number_of_shards" : 1, "number_of_replicas" : 0}'
sleep 1

echo "\nInstall the settings, requires closing the index first."
curl -XPOST "$ENDPOINT/$INDEX/_close"
sleep 1
curl -XPUT "$ENDPOINT/$INDEX/_settings" -d @../src/main/resources/elasticsearch/settings.json
sleep 1
echo "\nInstall the mapping - do once per type/mapping file."
curl -XPOST "$ENDPOINT/$INDEX/site/_mapping" -d @../src/main/resources/elasticsearch/mappings/site.json
sleep 1
curl -XPOST "$ENDPOINT/$INDEX/apiDoc/_mapping" -d @../src/main/resources/elasticsearch/mappings/apiDoc.json
sleep 1
curl -XPOST "$ENDPOINT/$INDEX/_open"
sleep 1

echo "\nCheck the settings."
curl $ENDPOINT/$INDEX/_settings?pretty=true
sleep 1

echo "\nCheck the mappings."
curl $ENDPOINT/$INDEX/_mapping?pretty=true
