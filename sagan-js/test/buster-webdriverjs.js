/**
 * BusterJS extension that injects a webdriverjs session into
 * tests, and adds a path() command for accessing urls relative
 * to a configured baseUrl.
 *
 * Example path command:
 *
 * browser.path('/') // fetch the root url of the site at baseUrl
 *
 * Example configuration for phantomjs:
 * 'buster-webdriverjs': {
 *   config: {
 *     desiredCapabilities: {
 *       browserName: 'phantomjs'
 *     },
 *     logLevel: 'silent'
 *   }
 * }
 *
 * Example config for sauce labs or any remote selenium server
 * {
 *    desiredCapabilities: {
 *      browserName: 'safari',
 *      version: '6',
 *      platform: 'Mac 10.8',
 *      tags: ['examples'],
 *      name: 'This is an example test',
 *      'tunnel-identifier': process.env.SAUCE_TUNNEL_ID // Sauce Labs only
 *    },
 *    host: 'ondemand.saucelabs.com',
 *    port: 80,
 *    user: process.env.SAUCE_USERNAME, // If user/pass required
 *    key: process.env.SAUCE_ACCESS_KEY, // If user/pass required
 *    logLevel: 'silent'
 *  }
 */

var url = require('url');

var defaultTimeout = 5000;

exports.name = 'buster-webdriverjs';

exports.create = function(options) {
  var ext = Object.create(this);
  ext.options = options;
  ext.webdriverConfig = options.config;
  return ext;
};

exports.testRun = function(testRunner) {
  var webdriverjs = require('webdriverjs');
  var timeout = this.options.timeout || defaultTimeout;
  var orig = webdriverjs.remote(this.webdriverConfig);
  var baseUrl = this.options.baseUrl || 'http://localhost:8080';
  var browser = Object.create(orig);

  browser.addCommand('path', function(relativeUrl, callback) {
    this.url(url.resolve(baseUrl, relativeUrl), callback);
  });

  browser.init();

  testRunner.on('test:setUp', function(context) {
    context.testCase.timeout = timeout;
    context.testCase.browser = browser;
  });

  testRunner.on('suite:end', function() {
    browser.end();
  });
};