var getAttributeValueOfVisible = require('../lib/wd/getAttributeValueOfVisible');

describe('Search Results page', function() {

    describe('facets', function() {

        describe('on page load', function() {

            it('should be empty set', function() {
                return this.browser.path('/search?q=rest')
                    .setImplicitWaitTimeout(5000)
                    .elementsByCssSelector('.sub-facet--list input[type=checkbox]')
                        .then(getAttributeValueOfVisible('checked'))
                        .then(function(values) {
                            expect(values.length).toBeGreaterThan(0);
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