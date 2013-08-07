#! /bin/sh

#
# Clear and re-configure the search index.
#
# An endpoint can be specified on the command line.

ENDPOINT=$1
if [ -z $ENDPOINT ]; then
	ENDPOINT=http://localhost:9200
fi

echo "\nDelete the existing index."
curl -XDELETE "$ENDPOINT/site"

echo "\nCreate the index again."
curl -X PUT $ENDPOINT/site -d '{ "number_of_shards" : 1, "number_of_replicas" : 0}'

echo "\nInstall the settings, requires closing the index first."
curl -XPOST "$ENDPOINT/site/_close"
curl -XPUT "$ENDPOINT/site/_settings" -d @../src/main/resources/config/elasticsearch/settings.json 

echo "\nInstall the mapping - do once per type/mapping file."
curl -XPOST "$ENDPOINT/site/site/_mapping" -d @../src/main/resources/config/elasticsearch/mappings/site.json 
curl -XPOST "$ENDPOINT/site/apiDoc/_mapping" -d @../src/main/resources/config/elasticsearch/mappings/apiDoc.json

curl -XPOST "$ENDPOINT/site/_open"


echo "\nCheck the settings."
curl $ENDPOINT/site/_settings?pretty=true

echo "\nCheck the mappings."
curl $ENDPOINT/site/_mapping?pretty=true
