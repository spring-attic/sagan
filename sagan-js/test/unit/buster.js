/**
 * Buster.JS configuration for JavaScript unit tests.
 */

// Basic unit test configuration for tests that can run
// in node (i.e. no DOM requirements)
exports.unit = {
  environment: 'node',
  tests: ['*-spec.js', '**/*-spec.js']
};
