var when = require('when');

/**
 * Curried function that returns a promise for an array of
 * attribute values of all *visible* elements
 */
module.exports = function getAttributeValueOfVisible(attributeName) {
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
};
