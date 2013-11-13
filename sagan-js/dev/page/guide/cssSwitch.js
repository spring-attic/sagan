/**
 * Creates a function that ensures that only one set of CSS classes is
 * applied at one time to an element.
 * @module
 * @type {Function}
 * @see cssSwitch
 */
module.exports = cssSwitch;

var $ = require('jquery');

/**
 * Accepts an element and a string of initial css classes that are *already
 * applied* to the node.  Returns a function that applies new classes while
 * removing the classes applied in a previous application of this function
 * (or as the initial classes).
 * @param {HTMLElement} node
 * @param {string} initialClasses
 * @returns {function(string): string}
 */
function cssSwitch (node, initialClasses) {
    var $node = $(node), prevClasses = initialClasses || '';
    return function (classes) {
        $node.removeClass(prevClasses);
        $node.addClass(classes);
        return classes;
    };
}
