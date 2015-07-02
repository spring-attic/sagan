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
curl -X PUT $ENDPOINT/$INDEX -d '{ "number_of_shards" : 5, "number_of_replicas" : 0}'
sleep 1
echo "\nClosing index."
curl -XPOST "$ENDPOINT/$INDEX/_close"
sleep 1
curl -XPUT $ENDPOINT/$INDEX/_settings -d @../sagan-site/src/main/resources/elasticsearch/settings.json
sleep 1
echo "\nInstall the mapping - do once per type/mapping file."
curl -XPOST "$ENDPOINT/$INDEX/apidoc/_mapping" -d @../sagan-site/src/main/resources/elasticsearch/mappings/apidoc.json
sleep 1
curl -XPOST "$ENDPOINT/$INDEX/blogpost/_mapping" -d @../sagan-site/src/main/resources/elasticsearch/mappings/blogpost.json
sleep 1
curl -XPOST "$ENDPOINT/$INDEX/guidedoc/_mapping" -d @../sagan-site/src/main/resources/elasticsearch/mappings/guidedoc.json
sleep 1
curl -XPOST "$ENDPOINT/$INDEX/referencedoc/_mapping" -d @../sagan-site/src/main/resources/elasticsearch/mappings/referencedoc.json
sleep 1
curl -XPOST "$ENDPOINT/$INDEX/sitepage/_mapping" -d @../sagan-site/src/main/resources/elasticsearch/mappings/sitepage.json
sleep 1
curl -XPOST "$ENDPOINT/$INDEX/projectpage/_mapping" -d @../sagan-site/src/main/resources/elasticsearch/mappings/projectpage.json
sleep 1
curl -XPOST "$ENDPOINT/$INDEX/_open"
sleep 1

echo "\nCheck the settings."
curl $ENDPOINT/$INDEX/_settings?pretty=true
sleep 1

echo "\nCheck the mappings."
curl $ENDPOINT/$INDEX/_mapping?pretty=true
