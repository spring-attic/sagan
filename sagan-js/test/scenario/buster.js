/**
 * Buster.JS configuration for JavaScript functional scenario tests.
 */

// Sniff environment for phantomjs path override. Otherwise, assume
// it's been installed via npm
var phantomjsBin = process.env.SELENIUM_PHANTOMJS_BIN || process.cwd() + '/node_modules/.bin/phantomjs';

// Shared config for all functional scenario tests
exports['scenario-base'] = {
  rootPath: '..',
  environment: 'node',
  tests: ['scenario/*-spec.js', 'scenario/**/*-spec.js']
};

// For now, a simple selenium setup that will use phantomjs
exports['scenario-phantom'] = {
  extends: 'scenario-base',
  extensions: [require('../lib/buster-webdriverjs')],
  testHelpers: ['lib/buster-spec-expose.js'],
  'buster-webdriverjs': {
    config: {
      desiredCapabilities: {
        browserName: 'phantomjs',
        'phantomjs.binary.path': phantomjsBin
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
