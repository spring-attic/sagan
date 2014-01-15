/*global sts_import */
var $ = require('jquery');

/**
 * The composition plan for pages that use the data-sts-import feature
 * attribute for exposing elements to STS.
 * @module
 */
module.exports = initStsImport;

function initStsImport () {

    var plan = {
        ready: ready,
        destroy: function () {}
    };

    /**
     * Finds the elements matching '.gs-guide-import', shows, them, and
     * adds a click handler to execute the globally available `sts_import`
     * function when inside STS.
     */
    function ready () {
        // `sts_import` is a global that the Spring Tool Suite inerts onto
        // the widow object when this page is run inside STS.
        if (typeof sts_import  === 'function') {
            $('.gs-guide-import').show().click(function (e) {
                e.preventDefault();
                sts_import('guide', e.target.href);
            });
        }
    }

    $(function () { plan.ready(); }.bind(plan));

    return plan;
}
