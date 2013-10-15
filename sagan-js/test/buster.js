/**
 * Buster.JS configuration for JavaScript unit and
 * functional scenario tests.
 */

require('./buster-spec-expose');

// Basic unit test configuration for tests that can run
// in node (i.e. no DOM requirements)
exports.unit = {
	environment: 'node',
	tests: ['unit/*-spec.js', 'unit/**/*-spec.js']
};
