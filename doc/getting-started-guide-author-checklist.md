
## copy and paste your way to working code
At every step in the guide, the use should have code that compiles. In the end, the user should have code that runs.

## demonstrate what 'working code' means
Always end the guide with a final step that exercises the working code. For example, a guide that stands up a RESTful web service should exercise that service using 

## always create 'scratch' and 'initial' anchors

## naming

use `initial` and `complete` for directory names

use group id `org.springframework`

in the `initial/pom.xml`, use `<artifactId>gs-${name}</artifactId>`, e.g. `gs-rest-service`

in the `complete/pom.xml`, use `<artifactId>gs-${name}-complete</artifactId>, e.g. `gs-rest-service-complete`

## versioning

use 1.0-SNAPSHOT for all versions

## use a common .gitignore

can probably create this from a gs-template project after all...
for now, use the .gitignore in gs-rest-service

## use consistent versioning in links

3.2.x vs current, etc