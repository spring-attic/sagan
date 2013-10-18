/**
 * Buster.JS configuration for JavaScript functional scenario tests.
 */

// Shared config for all functional scenario tests
exports['scenario-base'] = {
  rootPath: '..',
  environment: 'node',
  tests: ['scenario/*-spec.js', 'scenario/**/*-spec.js']
};

// For now, a simple selenium setup that will use phantomjs
exports['scenario-phantom'] = {
  extends: 'scenario-base',
  extensions: [require('../buster-webdriverjs')],
  testHelpers: ['buster-spec-expose.js'],
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
