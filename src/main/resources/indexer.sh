#!/bin/sh

CP=.
for f in lib/*.jar; do
  CP=$CP:"$f"
done

java -Xmx1260m -XX:MaxPermSize=512m -classpath "$CP" org.springframework.indexer.configuration.IndexerConfiguration $*