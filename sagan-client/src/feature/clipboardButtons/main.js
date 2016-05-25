module.exports = initClipboard;

var Clipboard = require('clipboard');
var $ = require('jquery');
var os = require('../../platform/os');
// This version of bootstrap looks for window.jQuery. ick.
window.jQuery = $;
require('bootstrap/js/bootstrap-tooltip');
var snippetsSelector = '.listingblock pre, .has-copy-button pre, article .highlight pre';

function initClipboard() {

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        $(snippetsSelector).each(function (index) {
                buildCopyButton($(this), index);
            }
        );
        var errorMessage = function () {
            if (os.type() == 'iOS') {
                return 'Could not copy :-(';
            }
            else if (os.type() == 'Mac') {
                return 'Press Cmd-C to Copy';
            }
            else {
                return 'Press Ctrl-C to Copy';
            }
        };
        var snippets = new Clipboard('.copy-button');
        snippets.on('success', function (e) {
            e.clearSelection();
            showTooltip(e.trigger, "Copied!");
        });
        snippets.on('error', function (e) {
            showTooltip(e.trigger, errorMessage());
        });
    }

    function showTooltip(elem, message) {
        $(elem).tooltip({placement: 'right', title: message});
        $(elem).tooltip('show');
        setTimeout(function () {
            $(elem).tooltip('destroy');
        }, 1000);
    }

    function buildCopyButton(preEl, id) {
        var codeBlockId = "code-block-" + id;
        var copyButtonId = "copy-button-" + id;
        preEl.attr('id', codeBlockId);
        var button = $('<button class="copy-button snippet" id="' + copyButtonId
            + '" data-clipboard-target="#' + codeBlockId + '"></button>');
        preEl.before(button);
    }

    function destroy() {
    }

}