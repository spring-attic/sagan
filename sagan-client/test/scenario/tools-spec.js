var when = require('when');
var asserters = require('wd').asserters;
var countDisplayed = require('../lib/wd/countDisplayed');

describe('Tools', function () {

    it('should have platform-specific download links', function() {
        // This test is surprisingly (overly??) complex!
        // It needs to verify that there are 2 download links, 1 for sts
        // and one for ggts.  However, the only way that I can see to
        // distinguish them right now, is to dig down to the <a>s and
        // inspect their href attribute, trying to ensure that one "looks like"
        // an sts download link, and the other "looks like" at ggts download
        // link.
        return this.browser.path('/tools')
            .waitForElementByCss('.download-links a', 5000)
            .elementsByCss('.download-links a')
            .then(function(elements) {
                return when.reduce(elements, function(displayed, el) {
                    return el.isDisplayed().then(function (isDisplayed) {
                        if (isDisplayed) {
                            displayed.push(el.getAttribute('href'));
                        }
                        return displayed;
                    });
                }, []).then(when.all);
            })
            .then(function(urls) {
                /*jshint sub:true*/
                expect(urls.length).toBe(2);
                var matches = urls.reduce(function(matches, url) {
                    if(/spring-tool-suite/i.test(url)) {
                        matches['sts'] = true;
                    } else if (/groovy-grails-tool-suite/i.test(url)) {
                        matches['ggts'] = true;
                    }

                    return matches;
                }, {});

                expect(matches['sts']).toBeTrue();
                expect(matches['ggts']).toBeTrue();
            });
    });

    describe('sts', function() {
        it('should have platform-specific download links', function() {
            return expectVisiblePlatformDownload(this.browser.path('/tools/sts'));
        });
    });

    describe('ggts', function() {
        it('should have platform-specific download links', function() {
            return expectVisiblePlatformDownload(this.browser.path('/tools/ggts'));
        });
    });
});

function expectVisiblePlatformDownload(browser) {
    return browser.waitForElementByCss('.download-links li', 5000)
        .elementsByCss('.download-links li')
        .then(countDisplayed).then(function(displayCount) {
            console.log('HERE', displayCount);
            expect(displayCount).toBe(1);
        });
}