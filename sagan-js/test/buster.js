/**
 * Buster.JS configuration for JavaScript unit and
 * functional scenario tests.
 */

// Basic unit test configuration for tests that can run
// in node (i.e. no DOM requirements)
exports.unit = {
  environment: 'node',
  tests: ['unit/*-spec.js', 'unit/**/*-spec.js']
};

// Shared config for all functional scenario tests
exports['scenario-base'] = {
  environment: 'node',
  tests: ['scenario/*-spec.js', 'scenario/**/*-spec.js']
};

// For now, a simple selenium setup that will use phantomjs
exports['scenario-phantom'] = {
  extends: 'scenario-base',
  extensions: [require('./buster-webdriverjs')],
  'buster-webdriverjs': {
    driver: 'webdriverjs',
    config: {
      desiredCapabilities: {
        browserName: 'phantomjs'
      },
      logLevel: 'silent'
    }
  }
};

// TODO: Enable and configure once selenium server is setup
//exports['scenario'] = {
//  extends: 'scenario-base',
//  extensions: [require('./buster-webdriverjs')],
//  'buster-webdriverjs': {
//    driver: 'webdriverjs',
//    config: {
//      // TODO: Change these
//      desiredCapabilities: {
//        browserName: 'safari',
//        version: '6',
//        platform: 'Mac 10.8'
//      },
//      host: '<selenium server hostname here>',
//      port: 80,
//      // If needed, use environment vars
//      user: process.env.WHATEVER,
//      key: process.env.WHATEVER,
//      logLevel: 'silent'
//    }
//  }
//};
