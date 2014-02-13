var getAttributeValueOfDisplayed = require('../lib/wd/getAttributeValueOfDisplayed');

describe('Search Results page', function() {

    describe('facets', function() {

        describe('on page load', function() {

            it('should be empty set', function() {
                return this.browser.path('/search?q=rest')
                    .setImplicitWaitTimeout(5000)
                    .elementsByCssSelector('.sub-facet--list input[type=checkbox]')
                        .then(getAttributeValueOfDisplayed('checked'))
                        .then(function(values) {
                            // Fold all the values to a single boolean
                            var result = values.reduce(function(result, value) {
                                return value || result;
                            }, false);
                            expect(result).toBeFalse();
                        });
            });

            it('should be collapsed', function() {
                return this.browser.path('/search?q=rest')
                    .setImplicitWaitTimeout(5000)
                    .elementsByCssSelector('.search-facets input[checked]')
                        .then(function(elements) {
                            expect(elements.length).toEqual(0);
                        });

            });

        });

    });
});