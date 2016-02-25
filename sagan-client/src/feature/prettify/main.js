var $ = require('jquery');
var prettify = require("google-code-prettify/bin/prettify.min.js");

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
        ready: function() {
            var code = null;
            $('pre').addClass('prettyprint').each(function(idx, el){
                code = el.firstChild;
                code.innerHTML = prettify.prettyPrintOne(code.innerHTML);
            });
        },
        destroy: function () {}
    };

    $(function () { plan.ready(); }.bind(plan));

    return plan;
}
