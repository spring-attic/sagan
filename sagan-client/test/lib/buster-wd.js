/**
 * BusterJS extension that injects a wd browser session into
 * tests, and adds a path() command for accessing urls relative
 * to a configured baseUrl (see Configuration below).
 *
 * Example path command:
 *
 * browser.path('/') // fetch the root url of the site at baseUrl
 *
 * Configuration
 *
 * The supported config options are:
 *
 * baseUrl (default 'http://localhost:8080') - base url (as a string) when
 *   using the browser.path command (above)
 * timeout (default 10000) - milliseconds to wait for pages to load before failing
 * config - webdriver-specific configuration options, see examples below
 *
 * Example configuration for phantomjs:
 * 'buster-wd': {
 *   config: {
 *     desiredCapabilities: {
 *       browserName: 'phantomjs'
 *     },
 *     logLevel: 'silent'
 *   }
 * }
 *
 * Example config for sauce labs or any remote selenium server
 * 'buster-wd': {
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
var webdriverPackage = 'wd';

var url = require('url');

var defaultBaseUrl = 'http://localhost:8080';
var defaultTimeout = 10000;

exports.name = 'buster-wd';

exports.create = function (options) {
    var ext = Object.create(this);
    ext.options = options;
    ext.webdriverConfig = options.config;
    return ext;
};

exports.testRun = function (testRunner) {
    var browser = createBrowser(require(webdriverPackage),
        this.options,
        this.webdriverConfig.desiredCapabilities);

    var timeout = this.options.timeout || defaultTimeout;

    testRunner.on('test:setUp', function (context) {
        context.testCase.timeout = timeout;
        context.testCase.browser = browser;
    });

    testRunner.on('suite:end', function () {
        browser.quit();
    });
};

function createBrowser(webdriver, options, capabilities) {
    var baseUrl = options.baseUrl || defaultBaseUrl;

    webdriver.webdriver.prototype.path = function (relativeUrl, callback) {
        this.get(url.resolve(baseUrl, relativeUrl), callback);
    };

    return webdriver.promiseChainRemote(options.server).init(capabilities);
}
