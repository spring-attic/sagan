# JavaScript Tests

This directory contains JavaScript unit tests and functional scenario tests, both of which are driven by the [Buster.JS](http://busterjs.org) testing framework.  The functional tests also require a [Selenium](http://www.seleniumhq.org) server.

## Unit tests

Tests live in the `unit` directory in a parallel structure to the sources.  Run the unit tests from the *root dir* of the project:

`npm test`

### BusterJS docs

http://docs.busterjs.org/en/latest/#user-s-guide

## Functional Scenario Tests

Tests live in the `scenario` directory.  Until our dedicated selenium server is ready, you can run them locally using Selenium Server and phantomjs:

1. Start sagan-site
    1. `sagan-site/run.sh`
1. Start selenium server:
    1. [Get the latest JAR here](http://code.google.com/p/selenium/downloads/list) if you need
    1. `java -jar <selenium-server-standalone JAR you just downloaded>`
1. Run the tests in sagan-js
    1. `cd sagan-js`
        1. If necessary, make sure you have the latest dependencies: `npm install`
    1. `npm run firefox`

### Using phantomjs instead of Firefox

You can run the scenario tests in headless mode against phantomjs:

`npm run phantom`

However, there seems to be a problem with phantomjs always reporting that some elements are "not visible".  Selenium will refuse to click such elements, and tests will fail because of it (the same tests will pass in Firefox and other browsers).

You may still want to run tests against phantomjs selectively, i.e. for quick iterations while working on a particular test.  Probably the best way is using BusterJS's [focus rocket](http://asciinema.org/a/548) to force buster to run only the test you're working on.

### Docs for wd selenium driver

https://github.com/admc/wd

## Tips

### More verbose test output

Buster's default output is extremely minimal: a single line showing progress and the number of tests, failures, errors, and successes so far.  After tests are done, it will report verbose info about test failures.

If you want more verbose output *while it's running*, in `package.json`, you can *temporarily* add `-r specification` to enable Buster's "specification" style reporter.  It will print out the names of tests as it runs them.  For example, you can change:

`"phantom": "buster-test -c test/scenario/buster.js -g scenario-phantom",`

to

`"phantom": "buster-test -c test/scenario/buster.js -g scenario-phantom -r specification",`
