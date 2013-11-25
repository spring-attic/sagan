var filter = require('./filter');
var createAttributeMatcher = require('./attributeMatcher');
var $ = require('jquery');

module.exports = function createFilterableList(onMatch, filterAttribute, containers) {
    var filterMatches = filter.create(
        onMatch,
        containers.map(function (node) {
            return {
                node: node,
                children: $('[' + filterAttribute + ']', node).get()
            };
        })
    );

    var attributeMatcher = createAttributeMatcher(filterAttribute);

    return function filterTargetValue(e) {
        return filterMatches(attributeMatcher(e.target.value));
    };
};