describe('Guide page', function() {

    before(function () {
        return this.browser.path('/guides/gs/rest-service/');
    });

    describe('code download type', function() {

        it('should show selected download type when clicked', function() {

            return this.browser
                .elementByCssSelector('.github-actions [data-protocol=https]')
                    .click()
                .elementByCssSelector('.clone-url.ssh')
                    .isVisible()
                    .then(function(visible) {
                        expect(visible).toBeFalse();
                    })
                .elementByCssSelector('.github-actions [data-protocol=ssh]')
                    .click()
                .elementByCssSelector('.clone-url.ssh')
                    .isVisible()
                    .then(function(visible) {
                        expect(visible).toBeTrue();
                    });
        });

    });

    describe('copy to clipboard buttons', function() {

        it('should be present on all listingblocks', function() {
            var expected;

            return this.browser
                // First, count the listing blocks
                .elementsByCssSelector('.listingblock')
                    .then(function(listings) {
                        expected = listings.length;
                        // This is here to ensure we're testing against a page
                        // that actually has code listings in it. If this ever
                        // fails, we should probably pick a different page.
                        expect(expected).toBeGreaterThan(0);
                    })
                // Verify that each one has copy button
                .elementsByCssSelector('.listingblock .copy-button')
                    .then(function(buttons) {
                        expect(buttons.length).toEqual(expected);
                    });
        });

        itEventually('should copy the listingblock text', function() {
            // TODO: How to implement this??
            // Selenium provides no access to the clipboard, and neither
            // does JavaScript.  The best I've thought of so far is:
            // 1. Execute remote JS to create a textarea
            // 1. Use wedbriver to click a clipboard button
            // 1. focus the textarea
            // 1. Send ctrl-v keys (will that work on OS X? will selenium
            //    do the mapping for us?)
            // 1. Get the textarea's value and compare it to the clicked
            //    button's data-clipboard-text attribute value
        });

    });
});