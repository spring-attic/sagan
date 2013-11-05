/**
 * guides page functional tests
 */

// For advanced promise combinators like map, reduce.
var when = require('when');

var filterInputId = 'doc_filter';
var filterAttribute = 'data-filterable';

describe('Guides page', function () {

    before(function () {
        return this.browser.path('/guides');
    });

    describe('filter field', function () {

        itEventually('should be focused', function () {
            // Once the autofocus is fixed, change itEventually to it
            return this.browser
                .keys('upload')
                .elementById(filterInputId)
                    .getValue().then(function (value) {
                        expect(value).toBe('upload');
                    });
        });

        it('should filter results when filter text matches', function () {
            var filterText = 'upload';

            return this.browser
                .elementById(filterInputId)
                    .type(filterText)
                .elementsByCssSelector('[' + filterAttribute + ']')
                    .then(getAttributeValueOfVisible(filterAttribute))
                    .then(function (attributeValues) {
                        attributeValues.forEach(function (value) {
                            expect(value.toLowerCase()).toContain(filterText);
                        });
                    });

        });

        it('should show no results when filter text doesn\'t match', function () {
            var filterText = 'l;kjsdfl;kjsal;dkfjlsdjf';

            return this.browser
                .elementById(filterInputId)
                    .type(filterText)
                .elementsByCssSelector('[' + filterAttribute + ']')
                    .then(getAttributeValueOfVisible(filterAttribute))
                    .then(function (attributeValues) {
                        expect(attributeValues.length).toBe(0);
                    });

        });
    });

});

/**
 * Curried function that returns a promise for an array of
 * attribute values of all *visible* elements
 */
function getAttributeValueOfVisible(attributeName) {
    return function (elements) {
        // Essentially simulating array.filter, but because everything
        // is async, we lean on when.reduce for the heavy lifting.
        // This reduce produces a smaller array of promises.
        // Use when.all at the end to ensure each promise in the array
        // has resolved.
        return when.reduce(elements,function (visible, element) {
            return element.isVisible().then(function (isVisible) {
                if (isVisible) {
                    visible.push(element.getAttribute(attributeName));
                }
                return visible;
            });
        }, []).then(when.all);
    };
}
