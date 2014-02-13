/**
 * guides page functional tests
 */

var getAttributeValueOfDisplayed = require('../lib/wd/getAttributeValueOfDisplayed');

var filterInputId = 'doc_filter';
var filterAttribute = 'data-filterable';

describe('Guides page', function () {

    before(function () {
        return this.browser.path('/guides');
    });

    describe('filter field', function () {

        it('should be focused', function () {
            // Once the autofocus is fixed, change itEventually to it
            return this.browser
                .setImplicitWaitTimeout(5000)
                    .active()
                    .getAttribute('id').then(function(id) {
                        expect(id).toBe('doc_filter');
                    });
        });

        it('should filter results when filter text matches', function () {
            var filterText = 'upload';

            return this.browser
                .elementById(filterInputId)
                    .type(filterText)
                .elementsByCssSelector('[' + filterAttribute + ']')
                    .then(getAttributeValueOfDisplayed(filterAttribute))
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
                    .then(getAttributeValueOfDisplayed(filterAttribute))
                    .then(function (attributeValues) {
                        expect(attributeValues.length).toBe(0);
                    });

        });
    });

});
