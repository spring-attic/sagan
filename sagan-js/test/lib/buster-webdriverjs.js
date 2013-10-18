/**
 * BusterJS extension that injects a webdriverjs session into
 * tests, and adds a path() command for accessing urls relative
 * to a configured baseUrl (which defaults to localhost:8080).
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

// This will be lazily require()d below
var webdriverPackage = 'webdriverjs';

var url = require('url');

var defaultBaseUrl = 'http://localhost:8080';
var defaultTimeout = 5000;

exports.name = 'buster-webdriverjs';

exports.create = function(options) {
  var ext = Object.create(this);
  ext.options = options;
  ext.webdriverConfig = options.config;
  return ext;
};

exports.testRun = function(testRunner) {
  var browser = createBrowser(require(webdriverPackage),
      this.webdriverConfig, this.options);

  var timeout = this.options.timeout || defaultTimeout;

  testRunner.on('test:setUp', function(context) {
    context.testCase.timeout = timeout;
    context.testCase.browser = browser;
  });

  testRunner.on('suite:end', function() {
    browser.end();
  });
};

function createBrowser(webdriver, config, options) {
  var orig = webdriver.remote(config);
  var baseUrl = options.baseUrl || defaultBaseUrl;
  var browser = Object.create(orig);

  browser.addCommand('path', function (relativeUrl, callback) {
    this.url(url.resolve(baseUrl, relativeUrl), callback);
  });

  browser.init();
  return browser;
}
