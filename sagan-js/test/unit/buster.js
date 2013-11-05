/**
 * Buster.JS configuration for JavaScript unit tests.
 */

// Basic unit test configuration for tests that can run
// in node (i.e. no DOM requirements)
exports.unit = {
    rootPath: '..',
    environment: 'node',
    tests: ['unit/*-spec.js', 'unit/**/*-spec.js'],
    testHelpers: ['lib/buster-spec-expose.js']
};
