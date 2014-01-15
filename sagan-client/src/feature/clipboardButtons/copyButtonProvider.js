/**
 * Creates clipboard buttons based off a prototype element.
 * @module
 * @type {Function}
 * @see copyButtonProvider
 */
module.exports = copyButtonProvider;

/**
 * Returns a function that will create clones of the provided prototype
 * element and insert them just before a "host" element.
 * @param {HTMLELement} prototype
 * @returns {function(HTMLELement) : HTMLELement}
 */
function copyButtonProvider (prototype) {
    return function appendCopyButton (host) {
        var button = prototype.cloneNode(true);
        // put it right before the host node
        host.parentNode.insertBefore(button, host);
        return button;
    };
}
