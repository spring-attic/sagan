# JavaScript Tests

This directory contains JavaScript unit tests and functional scenario tests, both of which are driven by the [Buster.JS](http://busterjs.org) testing framework.  The functional tests also require a [Selenium](http://www.seleniumhq.org) server.

## Unit tests

Tests live in the `unit` directory in a parallel structure to the sources.  Run the unit tests from the *root dir* of the project:

`npm test`

## Functional Scenario Tests

Tests live in the `scenario` directory.  Until our dedicated selenium server is ready, you can run them locally using Selenium Server and phantomjs:

1. Start sagan-site
    1. `sagan-site/run.sh`
1. Start selenium server:
    1. [Get the JAR here](http://www.seleniumhq.org/download/) if you need
    1. `java -jar selenium-server-standalone-2.35.0.jar`
1. Run the tests in sagan-js
    1. `cd sagan-js`
    1. `npm run phantom`