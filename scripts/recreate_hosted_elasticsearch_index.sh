#! /bin/sh

echo "\nDelete the existing index."
curl -XDELETE 'https://qlmsjwfa.api.qbox.io/site'

echo "\nCreate the index again."
curl -X PUT http://qlmsjwfa.api.qbox.io/site -d '{ "number_of_shards" : 1, "number_of_replicas" : 0}'

echo "\nInstall the settings, requires closing the index first."
curl -XPOST 'http://qlmsjwfa.api.qbox.io/site/_close'
curl -XPUT 'http://qlmsjwfa.api.qbox.io/site/_settings' -d @../src/main/resources/config/elasticsearch/settings.json 
curl -XPOST 'http://qlmsjwfa.api.qbox.io/site/_open'

echo "\nCheck the settings."
curl http://qlmsjwfa.api.qbox.io/site/_settings?pretty=true

echo "\nInstall the mapping - do once per type/mapping file."
curl -XPOST 'http://qlmsjwfa.api.qbox.io/site/site/_mapping' -d @../src/main/resources/config/elasticsearch/mappings/site.json 

echo "\nCheck the mappings."
curl http://qlmsjwfa.api.qbox.io/site/site/_mapping?pretty=true
