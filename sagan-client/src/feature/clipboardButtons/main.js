/**
 * The composition plan for clipboard components.
 * @module
 * @type {function(Object): Clipboard}
 * @see Clipboard
 */
module.exports = function () {
    return new Clipboard();
};

var ZeroClipboard = require('ZeroClipboard');
var clipboardProvider = require('./buttonConnector');
var copyButtonProvider = require('./copyButtonProvider');
var buttonTemplate = require('text!./button.html');
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

var ZeroClipboardSwf = '/lib/zeroclipboard/ZeroClipboard.swf';


// TODO: don't create clipboard buttons if ZeroClipboard.detectFlashSupport() is false
/**
 * Creates and initializes the plan for decorating pages that require
 * enhanced clipboard functionality.
 * @constructor
 */
function Clipboard () {
    var createButton;

    ZeroClipboard.setDefaults({ moviePath: ZeroClipboardSwf });

    createButton = copyButtonProvider($(buttonTemplate)[0]);
    this.createClipboardButtons = clipboardProvider(createButton);

    $(function () {
        this.ready();
    }.bind(this));
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
        return this.connectToClipboard(this.createClipboardButtons(elements));
    },

    /**
     * Adds enhanced clipboard functionality to the elements provided.
     * @param {HTMLElement|NodeList|Array} elements
     * @returns {*} elements
     */
    connectToClipboard: function (elements) {
        var zero = this.zero;
        zero.glue(elements);
        return elements;
    },

    /**
     * Called when the dom is ready.
     */
    ready: function () {
        var $copyButtons, $actionsSection, $codeSnippets;

        $codeSnippets = $('.listingblock pre, .has-copy-button pre, article .highlight pre');
        $actionsSection = $('.github-actions');
        $copyButtons = $actionsSection.find('button.copy-button');

        var zero = this.zero = new ZeroClipboard();
        // add bootstrap tooltip
        $(zero.htmlBridge).tooltip({
            title: 'copy to clipboard',
            placement: 'bottom'
        });

        this.connectToClipboard($copyButtons);
        this.attachClipboardElements($codeSnippets);

    },

    /**
     * Call this when this component is no longer needed.
     */
    destroy: function () {}

};
