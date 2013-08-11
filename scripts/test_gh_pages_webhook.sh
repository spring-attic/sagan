#!/bin/bash
#
# Poor man's test until this can be more properly run in an integration test
# without actually creating new GH issues every time.
#
# Run from root of project.
curl -X POST -d @src/test/resources/fixtures/github/ghPagesWebhook.json http://localhost:8080/webhook/gh-pages/default
