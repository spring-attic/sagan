var ClipboardJs = require('clipboard');
var $ = require('jquery');
var os = require('../../platform/os');

// This version of bootstrap looks for window.jQuery. ick.
window.jQuery = $;
require('bootstrap/js/bootstrap-tooltip');

var buttonTemplate = '<button class="copy-button snippet"></button>';
var snippetsSelector = '.listingblock pre, .has-copy-button pre, article .highlight pre';
var buttonsSelector = 'button.copy-button';

module.exports = function initClipboardButtons() {

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        var message = 'Copied to clipboard!';
        var createButton = copyButtonProvider($(buttonTemplate)[0]);
        var createClipboardButtons = buttonConnector(createButton);
        createClipboardButtons($(snippetsSelector));

        var clipboard = new ClipboardJs(buttonsSelector);
        clipboard.on('success', function(e) {
            $(e.trigger).tooltip('show');
            e.clearSelection();
        });

        clipboard.on('error', function(e) {
            if(os.type() == 'iOS') {
                message = 'Could not copy :-(';
            }
            else if (os.type == 'Mac') {
                message = 'Press ⌘-C to Copy';
            }
            else {
                message = 'Press Ctrl-C to Copy';
            }
            $(e.trigger).tooltip('show');
        });

        $(buttonsSelector).tooltip({
            title: function() {return message;},
            placement: 'bottom',
            trigger: 'manual'
        });

        $(buttonsSelector).on('shown.bs.tooltip', function () {
            setTimeout(function() {$(this).tooltip('hide');}.bind(this), 1000);
        });

    }

    function destroy() {
    }

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

    function buttonConnector (provideButton) {
        return function createClipboardElements(elements) {
            var el, button, text, buttons = [];
            for (var i = 0, len = elements.length; i < len; i++) {
                el = elements[i];
                button = provideButton(el);
                if (el.id) {
                    button.setAttribute('data-clipboard-target', el.id);
                }
                else {
                    text = el.value || el.textContent || el.innerText;
                    button.setAttribute('data-clipboard-text', text);
                }
                buttons.push(button);
            }
            return buttons;
        };
    }

};