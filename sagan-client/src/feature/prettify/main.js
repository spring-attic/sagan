var $ = require('jquery');
var prettify = require("google-code-prettify");

/**
 * The composition plan for the sidebar on pages that use the
 * data-code-prettify feature attribute.
 * @module
 */
module.exports = initPrettify;

/**
 * Creates and initializes the composition plan for the prettify library.
 * Calling this function will automatically prepare `ready` to be called
 * when the DOM is ready.
 * @returns {{ready: Function, destroy: Function}}
 */
function initPrettify () {

    var plan = {
        ready: prettify.prettyPrint,
        destroy: function () {}
    };

    $(function () { plan.ready(); }.bind(plan));

    return plan;
}
