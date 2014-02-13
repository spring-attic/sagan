var $ = require('jquery');

module.exports = Controller;

/**
 * Controller for an input view that can be shown/hidden
 * by adding/removing a provided showHideClass. The input
 * will be focused when shown and blurred when hidden.
 * @param {Node} root top-level element of the view
 * @param {string} showHideClass class that shows the view
 *  when added and hides it when removed
 * @constructor
 */
function Controller(root, showHideClass) {
    this.root = root;
    this._input = $('input', root).get(0);
    this._showHideClass = showHideClass;
}

/**
 * Ensure the view is hidden
 */
Controller.prototype.hide = function () {
    this._hide();
};

/**
 * Ensure the view is visible
 * @param {?string} additionalClass optional additional class or classes
 *  to add when showing the view.  These are in addition to the
 *  showHideClass that was provided to the constructor.
 */
Controller.prototype.show = function (additionalClass) {
    // Ensure previously added additional classes are removed
    // In most cases, this will be a noop (literally, the noop() function)
    // In the case where show() has been called twice in a row
    // without a call to hide() between, this will ensure that
    // additionallClass-es don't build up on the top-level view element.
    this._hide();

    var cls = this._showHideClass;
    if (additionalClass) {
        cls += ' ' + additionalClass;
    }

    // Since we know we're actually showing the view, setup _hide
    // to actually hide it, and to remove the additionalClass-es.
    this._hide = function () {
        $(this.root).removeClass(cls);
        this._input.blur();
        this._hide = noop;
    };

    $(this.root).addClass(cls);
    this._input.focus();
};

Controller.prototype._hide = noop;

function noop() {}