/**
 * The composition plan for clipboard components.
 * @module
 * @type {function(Object): Clipboard}
 * @see Clipboard
 */
module.exports = function (options) {
    return new Clipboard(options);
};

var ZeroClipboard = require('ZeroClipboard');
var clipboardProvider = require('./buttonConnector');
var $ = require('jquery');

// Bootstrap is included to implicitly import the tooltip plugin. ick.
require('bootstrap');

// ZeroClipboard 1.1.7 needs this
// I tried updating to ZeroClipboard 1.2.2 and it has major timing issues
// inside the swf file.  Many events are lost so most times, the
// clipboard doesn't have the right thing in it.  It typically will
// have no text or the text from previous previous copy-to-clipboard operation!
if (!window.ZeroClipboard) {
    window.ZeroClipboard = ZeroClipboard;
}

// TODO: don't create clipboard buttons if ZeroClipboard.detectFlashSupport() is false
/**
 * Creates and initializes the plan for decorating pages that require
 * enhanced clipboard functionality.
 * @constructor
 * @param options
 * @param {function(ZeroClipboard): undefined} [options.textProvider] overrides
 *     ZeroClipboard's default behavior for extracting text from the DOM. e.g.
 *     function (zero) {
 *         zero.setText(this.getAttribute('data-clipboard-text'));
 *     }
 * @param {function(HTMLElement): HTMLElement} options.buttonProvider
 *     creates a button HTML element.
 * @param {string} options['ZeroClipboard.swf'] specifies the location of the
 *     ZeroClipboard swf file.
 */
function Clipboard (options) {

    this.textProvider = options.textProvider;
    this.buttonProvider = options.buttonProvider;
    this.swf = options['ZeroClipboard.swf'];
    ZeroClipboard.setDefaults({ moviePath: this.swf });

    this.createClipboardElements = clipboardProvider(this.buttonProvider);

}

Clipboard.prototype = {

    /**
     * Attaches a clipboard button to each of the elements provided.
     * The clipboard buttons are created by the constructor's buttonProvider
     * option and given enhanced clipboard functionality.
     * @param {HTMLElement|NodeList|Array} elements
     * @returns {*} elements
     */
    attachClipboardElements: function (elements) {
        return this.connectToClipboard(this.createClipboardElements(elements));
    },

    /**
     * Adds enhanced clipboard functionality to the elements provided.
     * @param {HTMLElement|NodeList|Array} elements
     * @returns {*} elements
     */
    connectToClipboard: function (elements) {
        var zero = this.zero;
        zero.glue(elements);
        return zero;
    },

    /**
     * Call this when the dom is ready.
     */
    ready: function () {
        var zero = this.zero = new ZeroClipboard();
        // add bootstrap tooltip
        $(zero.htmlBridge).tooltip(
            {title: 'copy to clipboard', placement: 'bottom'}
        );
        if (this.textProvider) {
            zero.on('datarequested', this.textProvider);
        }
    },

    /**
     * Call this when this compoent is no longer needed.
     */
    destroy: function () {
        // TODO: remove event handlers
    }

};
