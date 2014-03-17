#!/bin/bash

# Script used by the Sagan build plan [1] for continuous blue/green deployment
# from spring-io/sagan:master [2] to the 'spring.io' organization at Pivotal Web
# Services [3]. Providing -Pproduction or -Pstaging determines the space to
# which the app will be deployed. Once deployment is complete, the sagan-site
# application can be accessed at http://[staging.]spring.io, and the sagan-indexer
# application, although essentially headless, can be found and verified at
# http://sagan-indexer-production.cfapps.io/info or
# http://sagan-indexer-staging.cfapps.io/info, respectively.
#
# The build plan at [1] is triggered by the 'Bamboo' commit hook at GitHub [4]
# whenever commits are pushed to spring-io/sagan:master, as opposed to the more
# common approach of polling the repository.
#
# The arguments to this script ($@) should be Gradle -P flags specifying login
# credentials and Cloud Foundry environment variables. If these properties are
# not provided as arguments, they will be looked for at
# ~/.gradle/sagan-env.gradle. For a complete list of required arguments, see
# ./gradle/deploy.gradle
#
# With the correct login credentials, environment variable values and
# permissions, this script should be suitable for use in deploying to any
# Cloud Foundry instance--whether at Pivotal Web Services or a private
# installation.
#
# [1]: https://build.spring.io/browse/IO-SAGAN
# [2]: https://github.com/spring-io/sagan
# [3]: https://console.run.pivotal.io/organizations/ca7b72bd-28fe-41b2-b453-3f9024bfa786
# [4]: https://github.com/spring-io/sagan/settings/hooks

if [ -z "$JAVA_HOME" ]; then
    echo "JAVA_HOME environment variable must be set. Exiting."
    exit 255;
fi;

GRADLE_OPTS="-Xmx1024m -Xms1024m"

./gradlew cf-login deploy $@
