var $ = require('jquery');

// This implicitly makes $.fn.datetimepicker available
// Unfortunate, but it's the jquery way.
require('bootstrap-datetimepicker');

/**
 * The composition plan for pages that use the data-form-widgets feature
 * attribute for decorating forms with jQuery plugins.
 * @module
 */
module.exports = initFormWidgets;

/**
 * Creates and initializes the composition plan for form widgets.
 * Calling this function will automatically prepare `ready` to be called
 * when the DOM is ready.
 * This plan replaces the inline script that was on the _post_Form.html
 * template.
 * @returns {{destroy: Function, ready: Function}}
 */
function initFormWidgets () {

    var plan = {
        ready: ready,
        destroy: destroy
    };

    function ready () {
        $('form .date').datetimepicker({ pickSeconds: false });
    }

    function destroy () {}

    $(function () { plan.ready(); }.bind(plan));

    return plan;
}
